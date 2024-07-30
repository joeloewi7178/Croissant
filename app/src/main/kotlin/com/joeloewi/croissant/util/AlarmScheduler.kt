package com.joeloewi.croissant.util

import com.joeloewi.croissant.core.model.Attendance

interface AlarmScheduler {
    fun scheduleCheckInAlarm(
        attendance: Attendance,
        scheduleForTomorrow: Boolean
    )

    fun cancelCheckInAlarm(
        attendanceId: Long,
    )
}