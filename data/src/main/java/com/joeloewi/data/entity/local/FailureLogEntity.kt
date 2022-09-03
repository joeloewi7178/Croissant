package com.joeloewi.data.entity.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
data class FailureLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val executionLogId: Long = 0,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
