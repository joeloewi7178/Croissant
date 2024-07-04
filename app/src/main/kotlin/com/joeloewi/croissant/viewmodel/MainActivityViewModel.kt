package com.joeloewi.croissant.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.global.GlobalDestination
import com.joeloewi.croissant.util.HourFormat
import com.joeloewi.croissant.util.isDeviceNexus5X
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    is24HourFormatImmediate: Boolean,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    is24HourFormat: SystemUseCase.Is24HourFormat,
    isDeviceRooted: SystemUseCase.IsDeviceRooted,
) : ViewModel(), ContainerHost<MainActivityViewModel.State, MainActivityViewModel.SideEffect> {
    private val _settings = getSettingsUseCase()
    private val _hourFormat = is24HourFormat().map {
        HourFormat.fromSystemHourFormat(it)
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HourFormat.fromSystemHourFormat(is24HourFormatImmediate)
    )
    private val _appUpdateResultState =
        flow {
            emit(Build.MODEL)
        }.filter {
            !isDeviceNexus5X()
        }.flatMapConcat {
            appUpdateManager.requestUpdateFlow()
        }.catch { cause ->
            Firebase.crashlytics.apply {
                log("AppUpdateManager")
                recordException(cause)
            }
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppUpdateResult.NotAvailable
        )
    private val _darkThemeEnabled = _settings.map {
        it.darkThemeEnabled
    }.catch {}.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )
    private val _isDeviceRooted = flow {
        emit(isDeviceRooted())
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    private val _isFirstLaunch =
        _settings.map { it.isFirstLaunch }.take(1).catch { }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    override val container: Container<State, SideEffect> = container(State()) {
        intent { _hourFormat.collect { reduce { state.copy(hourFormat = it) } } }
        intent { _appUpdateResultState.collect { reduce { state.copy(appUpdateResult = it) } } }
        intent { _darkThemeEnabled.collect { reduce { state.copy(darkThemeEnabled = it) } } }
        intent { _isDeviceRooted.collect { reduce { state.copy(isDeviceRooted = it) } } }
        intent { _isFirstLaunch.collect { reduce { state.copy(isFirstLaunch = it) } } }
    }

    fun onCurrentBackStackEntryChange(navBackStackEntry: NavBackStackEntry) = intent {
        reduce { state.copy(currentBackStackEntry = navBackStackEntry) }
        val currentBackStackEntry = state.currentBackStackEntry
        val destination = currentBackStackEntry?.destination

        val isNavigationRailVisible =
            currentBackStackEntry?.destination?.route in state.fullScreenDestinations && state.useNavRail && destination?.route == destination?.parent?.startDestinationRoute

        reduce { state.copy(isNavigationRailVisible = isNavigationRailVisible) }
    }

    fun onUseNavRailChange(useNavRail: Boolean) = intent {
        reduce { state.copy(useNavRail = useNavRail) }
    }

    data class State(
        val hourFormat: HourFormat = HourFormat.TwelveHour,
        val appUpdateResult: AppUpdateResult = AppUpdateResult.NotAvailable,
        val darkThemeEnabled: Boolean = false,
        val isDeviceRooted: Boolean = false,
        val isFirstLaunch: Boolean = false,
        val fullScreenDestinations: ImmutableList<String> = persistentListOf(
            AttendancesDestination.CreateAttendanceScreen.route,
            AttendancesDestination.LoginHoYoLabScreen.route,
            GlobalDestination.FirstLaunchScreen.route
        ),
        val croissantNavigations: ImmutableList<CroissantNavigation> = persistentListOf(
            CroissantNavigation.Attendances,
            CroissantNavigation.RedemptionCodes,
            CroissantNavigation.Settings
        ),
        val currentBackStackEntry: NavBackStackEntry? = null,
        val isNavigationRailVisible: Boolean = false,
        val isBottomNavigationBarVisible: Boolean = false,
        val useNavRail: Boolean = false,
    )

    sealed class SideEffect
}