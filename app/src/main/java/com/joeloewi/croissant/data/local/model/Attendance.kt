package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val title: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val sessionToken: String = "",
    val attendCheckInEventWorkerName: String = "",
    val checkSessionWorkerName: String = ""
)
