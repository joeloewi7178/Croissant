package com.joeloewi.croissant.ui.theme

import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun CroissantTheme(
    window: Window,
    content: @Composable () -> Unit
) {
    val useDarkIcons = !isSystemInDarkTheme()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = useDarkIcons
                isAppearanceLightNavigationBars = useDarkIcons
            }
        }
    }

    Mdc3Theme(
        content = content
    )
}