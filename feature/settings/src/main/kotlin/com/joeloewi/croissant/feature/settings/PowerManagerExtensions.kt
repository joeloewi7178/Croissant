package com.joeloewi.croissant.feature.settings

import android.content.Context
import android.os.Build
import android.os.PowerManager

fun PowerManager.isIgnoringBatteryOptimizationsCompat(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true
    }