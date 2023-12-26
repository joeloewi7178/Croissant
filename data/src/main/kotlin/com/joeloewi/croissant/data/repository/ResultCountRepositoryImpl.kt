package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.local.ResultCountDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultCount
import com.joeloewi.croissant.domain.repository.ResultCountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultCountRepositoryImpl @Inject constructor(
    private val resultCountDataSource: ResultCountDataSource
) : ResultCountRepository {

    override fun getAll(loggableWorker: LoggableWorker): Flow<List<ResultCount>> =
        resultCountDataSource.getAll(loggableWorker).flowOn(Dispatchers.IO)
}