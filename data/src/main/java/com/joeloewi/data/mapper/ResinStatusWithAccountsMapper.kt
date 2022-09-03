package com.joeloewi.data.mapper

import com.joeloewi.data.entity.local.relational.ResinStatusWidgetWithAccountsEntity
import com.joeloewi.data.mapper.base.ReadOnlyMapper
import com.joeloewi.domain.entity.relational.ResinStatusWidgetWithAccounts

class ResinStatusWithAccountsMapper(
    private val resinStatusWidgetMapper: ResinStatusWidgetMapper,
    private val accountMapper: AccountMapper
) : ReadOnlyMapper<ResinStatusWidgetWithAccounts, ResinStatusWidgetWithAccountsEntity> {
    override fun toDomain(dataEntity: ResinStatusWidgetWithAccountsEntity): ResinStatusWidgetWithAccounts =
        with(dataEntity) {
            ResinStatusWidgetWithAccounts(
                resinStatusWidget = resinStatusWidgetMapper.toDomain(resinStatusWidgetEntity),
                accounts = accountEntities.map { accountMapper.toDomain(it) }
            )
        }
}