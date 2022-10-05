package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.util.RootChecker
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val updateSettingsUseCase: SettingsUseCase.SetIsFirstLaunch,
    private val powerManager: PowerManager,
    private val alarmManager: AlarmManager,
    rootChecker: RootChecker,
) : ViewModel() {
    private val _settings = getSettingsUseCase()
    private val _isDeviceRooted = MutableStateFlow(rootChecker.isDeviceRooted())

    val isFirstLaunch = _settings.map { it.isFirstLaunch }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val isDeviceRooted = _isDeviceRooted.asStateFlow()

    val isIgnoringBatteryOptimizations
        get() = powerManager.isIgnoringBatteryOptimizationsCompat(application)

    val canScheduleExactAlarms
        get() = alarmManager.canScheduleExactAlarmsCompat()

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
}