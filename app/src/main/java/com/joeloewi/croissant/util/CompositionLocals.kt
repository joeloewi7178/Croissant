package com.joeloewi.croissant.util

import androidx.activity.ComponentActivity
import androidx.compose.runtime.compositionLocalOf

val LocalActivity = compositionLocalOf<ComponentActivity> {
    error("CompositionLocal LocalActivity not present")
}

val LocalHourFormat = compositionLocalOf<HourFormat> {
    error("CompositionLocal LocalHourFormat not present")
}