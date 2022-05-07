package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.FailureLogDataSource
import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.repository.FailureLogRepository
import javax.inject.Inject

class FailureLogRepositoryImpl @Inject constructor(
    private val failureLogDataSource: FailureLogDataSource
) : FailureLogRepository {
    override suspend fun insert(failureLog: FailureLog): Long =
        failureLogDataSource.insert(failureLog)
}