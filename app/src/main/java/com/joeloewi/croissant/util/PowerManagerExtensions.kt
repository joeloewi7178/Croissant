package com.joeloewi.croissant.util

import android.app.Application
import android.os.Build
import android.os.PowerManager

fun PowerManager.isIgnoringBatteryOptimizations(application: Application) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    isIgnoringBatteryOptimizations(application.packageName)
} else {
    true
}