package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.local.ResultRangeDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultRange
import com.joeloewi.croissant.domain.repository.ResultRangeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResultRangeRepositoryImpl @Inject constructor(
    private val resultRangeDataSource: ResultRangeDataSource
) : ResultRangeRepository {
    override fun getStartToEnd(loggableWorker: LoggableWorker): Flow<ResultRange> =
        resultRangeDataSource.getStartToEnd(loggableWorker)
}