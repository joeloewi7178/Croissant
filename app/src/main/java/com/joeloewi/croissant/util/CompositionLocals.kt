package com.joeloewi.croissant.util

import android.app.Activity
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalActivity = staticCompositionLocalOf<Activity> {
    error("CompositionLocal LocalActivity not present")
}

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    error("CompositionLocal LocalWindowSizeClass not present")
}

val LocalIs24HourFormat = compositionLocalOf<Boolean> {
    error("CompositionLocal LocalIs24HourFormat not present")
}