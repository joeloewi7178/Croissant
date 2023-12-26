package com.joeloewi.croissant.util

import android.content.Context
import android.os.Build
import android.os.PowerManager

fun PowerManager.isIgnoringBatteryOptimizationsCompat(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true
    }