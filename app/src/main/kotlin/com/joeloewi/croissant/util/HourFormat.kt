package com.joeloewi.croissant.util

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