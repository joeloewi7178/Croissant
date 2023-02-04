package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.util.RootChecker
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val isFirstLaunch = _settings.map { it.isFirstLaunch }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )
    val isDeviceRooted = flow {
        emit(rootChecker.isDeviceRooted())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    val isIgnoringBatteryOptimizations
        get() = powerManager.isIgnoringBatteryOptimizationsCompat(application)

    val canScheduleExactAlarms
        get() = alarmManager.canScheduleExactAlarmsCompat()
}