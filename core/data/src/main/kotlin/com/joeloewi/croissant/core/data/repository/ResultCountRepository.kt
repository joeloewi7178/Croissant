package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.ResultCount
import kotlinx.coroutines.flow.Flow

interface ResultCountRepository {

    fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>>
}