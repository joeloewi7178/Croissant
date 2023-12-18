package com.joeloewi.croissant.data.repository.local.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.data.database.dao.ResultCountDao
import com.joeloewi.croissant.data.repository.local.ResultCountDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.ResultCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultCountDataSourceImpl @Inject constructor(
    private val resultCountDao: ResultCountDao
) : ResultCountDataSource {
    override fun getAll(
        loggableWorker: LoggableWorker
    ): Flow<List<ResultCount>> {
        val query = """
            SELECT
                  DATE(createdAt / 1000, 'unixepoch', 'localtime') as date,
                  COUNT(CASE state WHEN "${WorkerExecutionLogState.SUCCESS.name}" THEN 1 ELSE NULL END) as successCount,
                  COUNT(CASE state WHEN "${WorkerExecutionLogState.FAILURE.name}" THEN 1 ELSE NULL END) as failureCount
            FROM (SELECT * FROM WorkerExecutionLogEntity ORDER BY attendanceId)
            WHERE loggableWorker = "${loggableWorker.name}"
            GROUP BY DATE((createdAt + 0.00) / 1000, 'unixepoch', 'localtime')
        """.trimIndent()

        return resultCountDao.getAll(
            SimpleSQLiteQuery(
                query,
                arrayOf<ResultCount>()
            )
        ).flowOn(Dispatchers.IO)
    }
}