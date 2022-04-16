package com.joeloewi.croissant.data.local.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.croissant.data.local.model.Reminder
import com.joeloewi.croissant.data.local.model.ScheduledDay

data class ReminderWithScheduledDays(
    @Embedded val reminder: Reminder = Reminder(),
    @Relation(
        parentColumn = "id",
        entityColumn = "reminderId"
    )
    val scheduledDays: List<ScheduledDay> = listOf()
)
