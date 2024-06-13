package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow

interface ResultCountRepository {

    fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>>
}