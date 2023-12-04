package com.joeloewi.croissant.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.SignStyle
import java.time.temporal.ChronoField
import java.util.Locale

fun dateTimeFormatterPerHourFormat(
    hourFormat: HourFormat
): DateTimeFormatter =
    when (hourFormat) {
        HourFormat.TwelveHour -> {
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
        }

        HourFormat.TwentyFourHour -> {
            DateTimeFormatterBuilder()
                .appendValue(ChronoField.HOUR_OF_DAY, 1, 2, SignStyle.NEVER)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .toFormatter(Locale.getDefault())
        }
    }