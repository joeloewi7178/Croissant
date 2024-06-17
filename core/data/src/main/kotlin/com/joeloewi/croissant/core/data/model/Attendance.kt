package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.database.model.AttendanceEntity
import java.util.TimeZone
import java.util.UUID

data class Attendance(
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val cookie: String = "",
    val nickname: String = "",
    val uid: Long = 0,
    val hourOfDay: Int = 0,
    val minute: Int = 0,
    val timezoneId: String = TimeZone.getDefault().id,
    val attendCheckInEventWorkerName: UUID = UUID.randomUUID(),
    val attendCheckInEventWorkerId: UUID = UUID.randomUUID(),
    val checkSessionWorkerName: UUID = UUID.randomUUID(),
    val checkSessionWorkerId: UUID = UUID.randomUUID(),
    val oneTimeAttendCheckInEventWorkerName: UUID = UUID.randomUUID(),
)

fun AttendanceEntity.asExternalData(): Attendance = with(this) {
    Attendance(
        id,
        createdAt,
        modifiedAt,
        cookie,
        nickname,
        uid,
        hourOfDay,
        minute,
        timezoneId,
        attendCheckInEventWorkerName,
        attendCheckInEventWorkerId,
        checkSessionWorkerName,
        checkSessionWorkerId,
        oneTimeAttendCheckInEventWorkerName
    )
}

fun Attendance.asData(): AttendanceEntity = with(this) {
    AttendanceEntity(
        id,
        createdAt,
        modifiedAt,
        cookie,
        nickname,
        uid,
        hourOfDay,
        minute,
        timezoneId,
        attendCheckInEventWorkerName,
        attendCheckInEventWorkerId,
        checkSessionWorkerName,
        checkSessionWorkerId,
        oneTimeAttendCheckInEventWorkerName
    )
}
