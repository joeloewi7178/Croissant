package com.joeloewi.croissant.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

fun Color.toCssRgba(): String =
    "rgba(${red * 255.0f}, ${green * 255.0f}, ${blue * 255.0f}, ${alpha})"

@Composable
fun rememberColorSchemeKey(
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme()
) = remember(isSystemInDarkTheme) {
    if (isSystemInDarkTheme) {
        "dark"
    } else {
        "light"
    }
}

//below sdk version 29, dark theme isn't adjusted properly
@Composable
fun rememberCssPrefersColorScheme(
    colorSchemeKey: String = rememberColorSchemeKey(),
    contentColor: Color = LocalContentColor.current
) = remember(
    colorSchemeKey,
    contentColor
) {
    "<style>" +
            "  @media (prefers-color-scheme: ${colorSchemeKey}) {" +
            "    p," +
            "    span," +
            "    strong {" +
            "      background-color: transparent !important;" +
            "      color: ${contentColor.toCssRgba()} !important;" +
            "    }" +
            "  }" +
            "</style>"
}