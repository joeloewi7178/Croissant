package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.AccountDao
import com.joeloewi.data.mapper.AccountMapper
import com.joeloewi.data.repository.local.AccountDataSource
import com.joeloewi.domain.entity.Account
import javax.inject.Inject

class AccountDataSourceImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val accountMapper: AccountMapper
) : AccountDataSource {

    override suspend fun insert(vararg accounts: Account): List<Long> =
        accountDao.insert(*accounts.map { accountMapper.toData(it) }.toTypedArray())
}