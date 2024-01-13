package com.joeloewi.croissant.data.entity.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.joeloewi.croissant.domain.common.HoYoLABGame

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AttendanceEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("attendanceId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val attendanceId: Long = 0,
    val roleId: Long = 0,
    val type: HoYoLABGame = HoYoLABGame.Unknown,
    val region: String = ""
)
