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
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val attendanceId: Long = 0,
    val roleId: Long = 0,
    val type: HoYoLABGame = HoYoLABGame.Unknown,
    val region: String = ""
)
