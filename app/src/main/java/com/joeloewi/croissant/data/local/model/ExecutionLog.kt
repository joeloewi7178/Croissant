package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joeloewi.croissant.data.common.HoYoLABGame

@Entity
data class ExecutionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val attendanceId: Long = Long.MIN_VALUE,
    val createdAt: Long = System.currentTimeMillis(),
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val code: Int = Int.MIN_VALUE,
    val message: String = ""
)
