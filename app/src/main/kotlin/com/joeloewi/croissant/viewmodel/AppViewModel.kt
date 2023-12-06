package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.isIgnoringBatteryOptimizationsCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val application: Application,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val powerManager: PowerManager,
    private val alarmManager: AlarmManager,

    ) : ViewModel() {
    private val _settings = getSettingsUseCase()


    val isIgnoringBatteryOptimizations
        get() = powerManager.isIgnoringBatteryOptimizationsCompat(application)

    val canScheduleExactAlarms
        get() = alarmManager.canScheduleExactAlarmsCompat()
}