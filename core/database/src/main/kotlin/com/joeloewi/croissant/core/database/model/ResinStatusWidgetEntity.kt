package com.joeloewi.croissant.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class ResinStatusWidgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val appWidgetId: Int = 0,
    val interval: Long = 0,
    val refreshGenshinResinStatusWorkerName: UUID = UUID.randomUUID()
)