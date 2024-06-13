package com.joeloewi.croissant.core.database

import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.flow.Flow

interface ResultCountDataSource {

    fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<com.joeloewi.croissant.core.data.model.ResultCount>>
}