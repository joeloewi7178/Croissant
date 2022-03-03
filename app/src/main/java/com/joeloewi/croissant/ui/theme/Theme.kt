package com.joeloewi.croissant.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun CroissantTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val localContext = LocalContext.current

    MaterialTheme(
        colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) {
                dynamicDarkColorScheme(localContext)
            } else {
                dynamicLightColorScheme(localContext)
            }
        } else {
            if (darkTheme) {
                darkColorScheme()
            } else {
                lightColorScheme()
            }
        },
        typography = Typography(),
        content = content
    )
}