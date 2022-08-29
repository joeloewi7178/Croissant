package com.joeloewi.data.entity.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.joeloewi.domain.common.HoYoLABGame

@Entity(
    indices = [Index("executionLogId")],
    foreignKeys = [
        ForeignKey(
            entity = WorkerExecutionLogEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("executionLogId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SuccessLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val retCode: Int = 0,
    val message: String = ""
)
