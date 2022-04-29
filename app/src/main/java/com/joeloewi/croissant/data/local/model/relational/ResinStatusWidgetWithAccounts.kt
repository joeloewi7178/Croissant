package com.joeloewi.croissant.data.local.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.croissant.data.local.model.Account
import com.joeloewi.croissant.data.local.model.ResinStatusWidget

data class ResinStatusWidgetWithAccounts(
    @Embedded val resinStatusWidget: ResinStatusWidget = ResinStatusWidget(),
    @Relation(
        parentColumn = "id",
        entityColumn = "resinStatusWidgetId"
    )
    val accounts: List<Account> = listOf()
)