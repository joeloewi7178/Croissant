package com.joeloewi.croissant.util

interface AlarmScheduler {
    fun scheduleCheckInAlarm(
        attendanceId: Long,
        hourOfDay: Int,
        minute: Int
    )

    fun cancelCheckInAlarm(
        attendanceId: Long,
    )
}