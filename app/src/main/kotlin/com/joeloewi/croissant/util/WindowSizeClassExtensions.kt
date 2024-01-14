package com.joeloewi.croissant.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

fun WindowSizeClass.useNavRail() =
    widthSizeClass > WindowWidthSizeClass.Compact