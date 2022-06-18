package com.joeloewi.croissant.util

import android.app.AlarmManager
import android.content.Context
import android.os.Build

fun Context.canScheduleExactAlarms() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    (getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
} else {
    true
}