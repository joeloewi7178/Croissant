package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow

interface ResultRangeRepository {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange>
}