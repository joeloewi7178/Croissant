package com.joeloewi.croissant.util

import android.app.AlarmManager
import android.os.Build

fun AlarmManager.canScheduleExactAlarmsCompat() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        canScheduleExactAlarms()
    } else {
        true
    }