package com.joeloewi.croissant.data.repository.local

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultCount
import kotlinx.coroutines.flow.Flow

interface ResultCountDataSource {

    fun getAll(
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>>
}