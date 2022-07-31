package com.joeloewi.data.entity.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.joeloewi.data.entity.AccountEntity
import com.joeloewi.data.entity.ResinStatusWidgetEntity

data class ResinStatusWidgetWithAccountsEntity(
    @Embedded
    val resinStatusWidgetEntity: ResinStatusWidgetEntity = ResinStatusWidgetEntity(),
    @Relation(
        parentColumn = "id",
        entityColumn = "resinStatusWidgetId"
    )
    val accountEntities: List<AccountEntity> = listOf()
)