package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class FailureLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val executionLogId: Long = 0,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
