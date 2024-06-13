package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.core.database.ResultRangeDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResultRangeRepositoryImpl @Inject constructor(
    private val resultRangeDataSource: ResultRangeDataSource
) : ResultRangeRepository {
    override fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange> = resultRangeDataSource.getStartToEnd(attendanceId, loggableWorker)
}