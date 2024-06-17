package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.core.data.model.asData
import com.joeloewi.croissant.core.data.model.asExternalData
import com.joeloewi.croissant.core.database.ResultRangeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResultRangeRepositoryImpl @Inject constructor(
    private val resultRangeDataSource: ResultRangeDataSource
) : ResultRangeRepository {
    override fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange> =
        resultRangeDataSource.getStartToEnd(attendanceId, loggableWorker.asData())
            .map { it.asExternalData() }
            .flowOn(Dispatchers.IO)
}