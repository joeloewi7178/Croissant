package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.database.model.AccountEntity

data class Account(
    val id: Long = 0,
    val resinStatusWidgetId: Long = 0,
    val cookie: String = "",
    val uid: Long = 0
)

fun AccountEntity.asExternalData(): Account = with(this) {
    Account(id, resinStatusWidgetId, cookie, uid)
}

fun Account.asData(): AccountEntity = with(this) {
    AccountEntity(id, resinStatusWidgetId, cookie, uid)
}
