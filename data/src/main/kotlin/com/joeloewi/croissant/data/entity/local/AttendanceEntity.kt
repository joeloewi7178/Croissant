package com.joeloewi.croissant.data.entity.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.TimeZone
import java.util.UUID

@Entity(
    indices = [
        Index(
            value = ["hourOfDay", "minute"],
            orders = [Index.Order.ASC, Index.Order.ASC]
        )
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val cookie: String = "",
    val nickname: String = "",
    @ColumnInfo(index = true)
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
