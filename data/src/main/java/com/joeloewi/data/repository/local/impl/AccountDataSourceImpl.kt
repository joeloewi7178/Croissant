package com.joeloewi.data.repository.local.impl

import com.joeloewi.data.db.dao.AccountDao
import com.joeloewi.data.entity.AccountEntity
import com.joeloewi.data.mapper.AccountMapper
import com.joeloewi.data.repository.local.AccountDataSource
import com.joeloewi.domain.entity.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountDataSourceImpl @Inject constructor(
    private val accountDao: AccountDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val accountMapper: AccountMapper
) : AccountDataSource {

    override suspend fun insert(vararg accounts: Account): List<Long> =
        withContext(coroutineDispatcher) {
            accountDao.insert(*accounts.map { accountMapper.toData(it) }.toTypedArray())
        }
}