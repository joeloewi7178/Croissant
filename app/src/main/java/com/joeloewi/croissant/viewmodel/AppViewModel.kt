package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.util.RootChecker
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    getSettingsUseCase: SettingsUseCase.GetSettings,
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
}