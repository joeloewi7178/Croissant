package com.joeloewi.data.mapper

import com.joeloewi.data.entity.AccountEntity
import com.joeloewi.data.mapper.base.Mapper
import com.joeloewi.domain.entity.Account

class AccountMapper : Mapper<Account, AccountEntity> {
    override fun toData(domainEntity: Account): AccountEntity = with(domainEntity) {
        AccountEntity(id, resinStatusWidgetId, cookie, uid)
    }

    override fun toDomain(dataEntity: AccountEntity): Account = with(dataEntity) {
        Account(id, resinStatusWidgetId, cookie, uid)
    }
}