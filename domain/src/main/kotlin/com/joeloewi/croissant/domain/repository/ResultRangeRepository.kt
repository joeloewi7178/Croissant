package com.joeloewi.croissant.domain.repository

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultRange
import kotlinx.coroutines.flow.Flow

interface ResultRangeRepository {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange>
}