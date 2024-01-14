package com.joeloewi.croissant.data.entity.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
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
    @ColumnInfo(index = true)
    val resinStatusWidgetId: Long = 0,
    val cookie: String = "",
    val uid: Long = 0
)
