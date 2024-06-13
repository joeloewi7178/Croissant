package com.joeloewi.croissant.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
