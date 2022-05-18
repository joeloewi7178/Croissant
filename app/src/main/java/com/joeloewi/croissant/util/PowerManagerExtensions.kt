package com.joeloewi.croissant.util

import android.content.Context
import android.os.Build
import android.os.PowerManager


fun Context.isIgnoringBatteryOptimizations() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    (getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
        packageName
    )
} else {
    true
}

fun Context.isPowerSaveMode() =
    (getSystemService(Context.POWER_SERVICE) as PowerManager).isPowerSaveMode