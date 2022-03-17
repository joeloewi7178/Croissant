package com.joeloewi.croissant.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joeloewi.croissant.data.common.HoYoLABGame

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledHour: Int = 0,
    val remindWorkerId: String = "",
    val clickToPlay: HoYoLABGame = HoYoLABGame.Unknown
)
