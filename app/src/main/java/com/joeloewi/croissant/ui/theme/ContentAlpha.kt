package com.joeloewi.croissant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.luminance

object ContentAlpha {
    val high: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.high,
            lowContrastAlpha = LowContrastContentAlpha.high
        )

    val medium: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.medium,
            lowContrastAlpha = LowContrastContentAlpha.medium
        )

    val disabled: Float
        @Composable
        get() = contentAlpha(
            highContrastAlpha = HighContrastContentAlpha.disabled,
            lowContrastAlpha = LowContrastContentAlpha.disabled
        )

    @Composable
    private fun contentAlpha(
        /*@FloatRange(from = 0.0, to = 1.0)*/
        highContrastAlpha: Float,
        /*@FloatRange(from = 0.0, to = 1.0)*/
        lowContrastAlpha: Float
    ): Float {
        val contentColor = LocalContentColor.current
        val lightTheme = !isSystemInDarkTheme()
        return if (lightTheme) {
            if (contentColor.luminance() > 0.5) highContrastAlpha else lowContrastAlpha
        } else {
            if (contentColor.luminance() < 0.5) highContrastAlpha else lowContrastAlpha
        }
    }
}

private object HighContrastContentAlpha {
    const val high: Float = 1.00f
    const val medium: Float = 0.74f
    const val disabled: Float = 0.38f
}

private object LowContrastContentAlpha {
    const val high: Float = 0.87f
    const val medium: Float = 0.60f
    const val disabled: Float = 0.38f
}