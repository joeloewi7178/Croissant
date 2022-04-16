package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.joeloewi.croissant.data.common.HoYoLABGame

@Entity(
    indices = [Index("executionLogId")],
    foreignKeys = [
        ForeignKey(
            entity = WorkerExecutionLog::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("executionLogId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SuccessLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val retCode: Int = 0,
    val message: String = ""
)
