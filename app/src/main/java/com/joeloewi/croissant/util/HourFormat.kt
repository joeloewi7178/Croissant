package com.joeloewi.croissant.util

import androidx.compose.runtime.Stable

@Stable
enum class HourFormat {
    TwelveHour, TwentyFourHour;

    companion object {
        fun fromSystemHourFormat(is24HourFormat: Boolean): HourFormat = if (is24HourFormat) {
            TwentyFourHour
        } else {
            TwelveHour
        }
    }
}