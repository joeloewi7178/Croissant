package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.local.ResultCountDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultCount
import com.joeloewi.croissant.domain.repository.ResultCountRepository
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