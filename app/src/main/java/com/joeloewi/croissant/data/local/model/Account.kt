package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("resinStatusWidgetId")],
    foreignKeys = [
        ForeignKey(
            entity = ResinStatusWidget::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("resinStatusWidgetId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Account(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val resinStatusWidgetId: Long = 0,
    val cookie: String = "",
    val uid: Long = 0
)
