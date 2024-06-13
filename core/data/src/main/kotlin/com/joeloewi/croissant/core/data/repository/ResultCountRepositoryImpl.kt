package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.core.database.ResultCountDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResultCountRepositoryImpl @Inject constructor(
    private val resultCountDataSource: ResultCountDataSource
) : ResultCountRepository {

    override fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>> =
        resultCountDataSource.getAll(attendanceId, loggableWorker)
}