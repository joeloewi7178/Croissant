package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ResinStatusWidget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appWidgetId: Int = 0,
    val interval: Long = 0,
    val refreshGenshinResinStatusWorkerName: UUID = UUID.randomUUID()
)