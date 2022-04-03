package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val cookie: String = "",
    val nickname: String = "",
    val hourOfDay: Int = 0,
    val minute: Int = 0,
    val attendCheckInEventWorkerName: String = UUID.randomUUID().toString(),
    val checkSessionWorkerName: String = UUID.randomUUID().toString()
)
