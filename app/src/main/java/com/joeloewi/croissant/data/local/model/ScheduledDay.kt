package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("reminderId")],
    foreignKeys = [
        ForeignKey(
            entity = Reminder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("reminderId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ScheduledDay(
    @PrimaryKey val id: Long = 0,
    val reminderId: Long = 0,
    val dayOfWeek: Int = 0
)
