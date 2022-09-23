package com.joeloewi.croissant.util

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<Activity> {
    error("CompositionLocal LocalActivity not present")
}

val LocalHourFormat = compositionLocalOf<HourFormat> {
    error("CompositionLocal LocalHourFormat not present")
}