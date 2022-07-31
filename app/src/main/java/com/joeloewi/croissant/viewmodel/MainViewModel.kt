package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.util.RootChecker
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val updateSettingsUseCase: SettingsUseCase.SetIsFirstLaunch,
    private val powerManager: PowerManager,
    private val alarmManager: AlarmManager,
    private val rootChecker: RootChecker,
) : ViewModel() {
    private val _settings = getSettingsUseCase()
    private val _isDeviceRooted = MutableStateFlow(false)

    val isFirstLaunch = _settings.map { it.isFirstLaunch }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val isDeviceRooted = _isDeviceRooted.asStateFlow()
    val appUpdateResultState =
        AppUpdateManagerFactory.create(application).requestUpdateFlow().catch { cause ->
            FirebaseCrashlytics.getInstance().apply {
                log("AppUpdateManager")
                recordException(cause)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AppUpdateResult.NotAvailable
        )

    init {
        _settings.map { it.darkThemeEnabled }.distinctUntilChanged()
            .onEach { darkThemeEnabled ->
                if (darkThemeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }.launchIn(viewModelScope)
    }

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            updateSettingsUseCase(isFirstLaunch)
        }
    }

    fun isIgnoringBatteryOptimizations() =
        powerManager.isIgnoringBatteryOptimizations(application.packageName)

    fun canScheduleExactAlarms() = alarmManager.canScheduleExactAlarms()

    fun checkIsDeviceRooted() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDeviceRooted.update { rootChecker.isDeviceRooted() }
        }
    }
}