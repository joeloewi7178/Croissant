package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.Account

interface AccountRepository {
    suspend fun insert(vararg accounts: Account): List<Long>
}