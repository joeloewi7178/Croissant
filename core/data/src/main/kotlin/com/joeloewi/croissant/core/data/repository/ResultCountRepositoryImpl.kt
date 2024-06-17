package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.core.data.model.asData
import com.joeloewi.croissant.core.data.model.asExternalData
import com.joeloewi.croissant.core.database.ResultCountDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResultCountRepositoryImpl @Inject constructor(
    private val resultCountDataSource: ResultCountDataSource
) : ResultCountRepository {

    override fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>> =
        resultCountDataSource.getAll(attendanceId, loggableWorker.asData())
            .map { list -> list.map { it.asExternalData() } }
            .flowOn(Dispatchers.IO)
}