package com.joeloewi.croissant.domain.repository

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultCount
import kotlinx.coroutines.flow.Flow

interface ResultCountRepository {

    fun getAll(loggableWorker: LoggableWorker): Flow<List<ResultCount>>
}