package com.joeloewi.data.entity.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("resinStatusWidgetId")],
    foreignKeys = [
        ForeignKey(
            entity = ResinStatusWidgetEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("resinStatusWidgetId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val resinStatusWidgetId: Long = 0,
    val cookie: String = "",
    val uid: Long = 0
)
