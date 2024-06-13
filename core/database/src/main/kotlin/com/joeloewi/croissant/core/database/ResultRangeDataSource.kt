package com.joeloewi.croissant.core.database

import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow

interface ResultRangeDataSource {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<com.joeloewi.croissant.core.data.model.ResultRange>
}