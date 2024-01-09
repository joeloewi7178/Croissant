package com.joeloewi.croissant.util

import com.joeloewi.croissant.domain.entity.Attendance

interface AlarmScheduler {
    fun scheduleCheckInAlarm(
        attendance: Attendance,
        scheduleForTomorrow: Boolean
    )

    fun cancelCheckInAlarm(
        attendanceId: Long,
    )
}