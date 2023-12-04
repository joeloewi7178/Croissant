package com.joeloewi.croissant.ui.theme

import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.joeloewi.croissant.BuildConfig

@Composable
fun CroissantTheme(
    window: Window,
    content: @Composable () -> Unit
) {
    val useDarkIcons = !isSystemInDarkTheme()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = useDarkIcons
                isAppearanceLightNavigationBars = useDarkIcons
            }

            if (BuildConfig.DEBUG) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                )
            }
        }
    }

    Mdc3Theme(
        content = content
    )
}

val successContainerColor
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0xFF00522C)
    } else {
        Color(0xFF97F7B6)
    }

val onSuccessContainerColor
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0xFF7CDA9C)
    } else {
        Color(0xFF006D3C)
    }

val warningContainerColor
    @Composable
    get() = if (isSystemInDarkTheme()) {
        Color(0xFF564500)
    } else {
        Color(0xFFFFE083)
    }