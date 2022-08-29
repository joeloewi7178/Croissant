package com.joeloewi.data.entity.local.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.data.entity.local.AccountEntity
import com.joeloewi.data.entity.local.ResinStatusWidgetEntity

data class ResinStatusWidgetWithAccountsEntity(
    @Embedded
    val resinStatusWidgetEntity: ResinStatusWidgetEntity = ResinStatusWidgetEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "resinStatusWidgetId"
    )
    val accountEntities: List<AccountEntity> = listOf()
)