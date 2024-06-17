package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.ResultRange
import kotlinx.coroutines.flow.Flow

interface ResultRangeRepository {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange>
}