package com.joeloewi.croissant.core.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultCountDataSourceImpl @Inject constructor(
    private val resultCountDao: com.joeloewi.croissant.core.database.dao.ResultCountDao
) : com.joeloewi.croissant.core.database.ResultCountDataSource {
    override fun getAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<List<com.joeloewi.croissant.core.data.model.ResultCount>> {
        val query = """
            SELECT
                  DATE(createdAt / 1000, 'unixepoch', 'localtime') as date,
                  COUNT(CASE state WHEN '${WorkerExecutionLogState.SUCCESS.name}' THEN 1 ELSE NULL END) as successCount,
                  COUNT(CASE state WHEN '${WorkerExecutionLogState.FAILURE.name}' THEN 1 ELSE NULL END) as failureCount
            FROM (SELECT * FROM WorkerExecutionLogEntity ORDER BY attendanceId)
            WHERE loggableWorker = '${loggableWorker.name}'
                AND attendanceId = '${attendanceId}'
            GROUP BY DATE((createdAt + 0.00) / 1000, 'unixepoch', 'localtime')
        """.trimIndent()

        return resultCountDao.getAll(
            SimpleSQLiteQuery(
                query,
                arrayOf<com.joeloewi.croissant.core.data.model.ResultCount>()
            )
        ).flowOn(Dispatchers.IO)
    }
}