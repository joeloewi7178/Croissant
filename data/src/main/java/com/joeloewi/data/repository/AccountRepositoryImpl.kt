package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.AccountDataSource
import com.joeloewi.domain.entity.Account
import com.joeloewi.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDataSource: AccountDataSource
) : AccountRepository {
    override suspend fun insert(vararg accounts: Account): List<Long> =
        accountDataSource.insert(*accounts)
}