package com.joeloewi.croissant.core.database

import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.ResultRangeEntity
import kotlinx.coroutines.flow.Flow

interface ResultRangeDataSource {

    fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Flow<ResultRangeEntity>
}