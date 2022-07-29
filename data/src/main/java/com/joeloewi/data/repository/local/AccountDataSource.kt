package com.joeloewi.data.repository.local

import com.joeloewi.domain.entity.Account

interface AccountDataSource {
   suspend fun insert(vararg accounts: Account): List<Long>
}