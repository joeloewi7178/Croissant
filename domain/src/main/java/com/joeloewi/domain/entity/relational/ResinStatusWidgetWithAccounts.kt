package com.joeloewi.domain.entity.relational

import com.joeloewi.domain.entity.Account
import com.joeloewi.domain.entity.ResinStatusWidget

data class ResinStatusWidgetWithAccounts(
    val resinStatusWidget: ResinStatusWidget = ResinStatusWidget(),
    val accounts: List<Account> = listOf()
)
