package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.joeloewi.croissant.data.common.HoYoLABGame

@Entity(
    indices = [Index("attendanceId")],
    foreignKeys = [
        ForeignKey(
            entity = Attendance::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("attendanceId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExecutionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val attendanceId: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val code: Int = 0,
    val message: String = ""
)
