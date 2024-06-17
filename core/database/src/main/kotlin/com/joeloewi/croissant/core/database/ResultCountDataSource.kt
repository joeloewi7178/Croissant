package com.joeloewi.croissant.core.database

import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.ResultCountEntity
import kotlinx.coroutines.flow.Flow

interface ResultCountDataSource {

    fun getAll(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Flow<List<ResultCountEntity>>
}