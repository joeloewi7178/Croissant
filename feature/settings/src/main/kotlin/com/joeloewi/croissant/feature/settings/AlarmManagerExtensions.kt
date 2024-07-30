package com.joeloewi.croissant.feature.settings

import android.app.AlarmManager
import android.os.Build

fun AlarmManager.canScheduleExactAlarmsCompat() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        canScheduleExactAlarms()
    } else {
        true
    }