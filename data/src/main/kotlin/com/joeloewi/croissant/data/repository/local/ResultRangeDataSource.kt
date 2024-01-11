package com.joeloewi.croissant.data.repository.local

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultRange
import kotlinx.coroutines.flow.Flow

interface ResultRangeDataSource {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<ResultRange>
}