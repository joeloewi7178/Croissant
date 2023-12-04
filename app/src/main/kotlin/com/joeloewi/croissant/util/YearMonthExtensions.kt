package com.joeloewi.croissant.util

import java.time.YearMonth

//in calendar, there is blank to match day and day of week
fun YearMonth.generateCalendarDays(): List<Int> =
    Array(
        size = atDay(1).dayOfWeek.value % 7
    ) { 0 }.toMutableList() + (1..lengthOfMonth()) + Array(
        size = 6 - (atDay(lengthOfMonth()).dayOfWeek.value % 7)
    ) { 0 }.toMutableList()