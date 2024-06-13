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
data class SuccessLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val executionLogId: Long = 0,
    @ColumnInfo(index = true)
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val retCode: Int = 0,
    val message: String = ""
)
