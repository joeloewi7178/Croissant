package com.joeloewi.croissant.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReminderWithScheduledDays(
    @Embedded val reminder: Reminder = Reminder(),
    @Relation(
        parentColumn = "id",
        entityColumn = "reminderId"
    )
    val scheduledDays: List<ScheduledDay> = listOf()
)
