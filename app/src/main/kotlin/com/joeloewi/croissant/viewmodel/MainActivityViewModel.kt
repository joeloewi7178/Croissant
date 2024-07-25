package com.joeloewi.croissant.viewmodel

import android.os.Build
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.global.GlobalDestination
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.HourFormat
import com.joeloewi.croissant.util.isDeviceNexus5X
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
    is24HourFormatImmediate: Boolean,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    is24HourFormat: SystemUseCase.Is24HourFormat,
    isDeviceRooted: SystemUseCase.IsDeviceRooted,
    checkPermissions: SystemUseCase.CheckPermissions
) : ViewModel(), ContainerHost<MainActivityViewModel.State, MainActivityViewModel.SideEffect> {
    private val _settings = getSettingsUseCase()
    private val _hourFormat = is24HourFormat().onStart {
        emit(is24HourFormatImmediate)
    }.map {
        HourFormat.fromSystemHourFormat(it)
    }.catch { }.flowOn(Dispatchers.IO)
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
            started = SharingStarted.WhileSubscribed(),
            initialValue = AppUpdateResult.NotAvailable
        )
    private val _darkThemeEnabled = _settings.map {
        it.darkThemeEnabled
    }.catch {}.flowOn(Dispatchers.IO)
    private val _isDeviceRooted = flow {
        emit(isDeviceRooted())
    }.catch { }.flowOn(Dispatchers.IO)
    private val _isFirstLaunch =
        _settings.map { it.isFirstLaunch }.take(1).catch { }.flowOn(Dispatchers.IO)

    override val container: Container<State, SideEffect> = container(State()) {
        intent { _hourFormat.collect { reduce { state.copy(hourFormat = it) } } }
        intent { _darkThemeEnabled.collect { reduce { state.copy(darkThemeEnabled = it) } } }
        intent { _isDeviceRooted.collect { reduce { state.copy(isDeviceRooted = it) } } }
        intent { _isFirstLaunch.collect { reduce { state.copy(isFirstLaunch = it) } } }
    }

    init {
        intent {
            container.stateFlow.map { it.isFirstLaunch }.distinctUntilChanged()
                .collect { isFirstLaunch ->
                    val anyOfPermissionsIsDenied = checkPermissions(
                        CroissantPermission.AccessHoYoLABSession.permission,
                        CroissantPermission.PostNotifications.permission
                    ).any { !it.second }

                    val startDestination = if (isFirstLaunch || anyOfPermissionsIsDenied) {
                        GlobalDestination.FirstLaunchScreen.route
                    } else {
                        CroissantNavigation.Attendances.route
                    }

                    reduce {
                        state.copy(startDestination = LCE.Content(startDestination))
                    }
                }
        }

        intent {
            _appUpdateResultState.collect {
                when (it) {
                    is AppUpdateResult.Available -> {
                        postSideEffect(SideEffect.StartAppUpdate(it))
                    }

                    is AppUpdateResult.Downloaded -> {
                        it.completeUpdate()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    fun onClickConfirmClose() = intent {
        postSideEffect(SideEffect.FinishActivity)
    }

    fun onClickNavigationButton(route: String) = intent {
        postSideEffect(SideEffect.OnClickNavigationButton(route = route))
    }

    @Immutable
    data class State(
        val hourFormat: HourFormat = HourFormat.TwelveHour,
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
        val startDestination: LCE<String> = LCE.Loading,
        val route: String = "MainActivity"
    )

    @Immutable
    sealed class SideEffect {
        data object FinishActivity : SideEffect()
        data class StartAppUpdate(
            val appUpdateResult: AppUpdateResult.Available
        ) : SideEffect()

        data class OnClickNavigationButton(
            val route: String
        ) : SideEffect()
    }
}