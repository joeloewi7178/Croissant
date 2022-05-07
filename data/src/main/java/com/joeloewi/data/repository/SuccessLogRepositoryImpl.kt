package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.SuccessLogDataSource
import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.repository.SuccessLogRepository
import javax.inject.Inject

class SuccessLogRepositoryImpl @Inject constructor(
    private val successLogDataSource: SuccessLogDataSource
) : SuccessLogRepository {
    override suspend fun insert(successLog: SuccessLog): Long =
        successLogDataSource.insert(successLog)
}