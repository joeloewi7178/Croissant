package com.joeloewi.croissant.data.entity.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.joeloewi.croissant.domain.common.HoYoLABGame

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkerExecutionLogEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("executionLogId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FailureLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val executionLogId: Long = 0,
    @ColumnInfo(index = true, defaultValue = "Unknown")
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
