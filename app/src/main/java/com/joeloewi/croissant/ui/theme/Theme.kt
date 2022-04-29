package com.joeloewi.croissant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun CroissantTheme(
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    SideEffect {
        systemUiController.apply {
            setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
            setStatusBarColor(Color.Transparent, darkIcons = useDarkIcons)
            setNavigationBarColor(Color.Transparent, darkIcons = useDarkIcons)
        }
    }

    Mdc3Theme(
        content = content
    )
}