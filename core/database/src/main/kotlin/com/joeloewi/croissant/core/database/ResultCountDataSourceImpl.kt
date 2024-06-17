package com.joeloewi.croissant.core.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.core.database.dao.ResultCountDao
import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.DataWorkerExecutionLogState
import com.joeloewi.croissant.core.database.model.ResultCountEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultCountDataSourceImpl @Inject constructor(
    private val resultCountDao: ResultCountDao
) : ResultCountDataSource {
    override fun getAll(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Flow<List<ResultCountEntity>> {
        val query = """
            SELECT
                  DATE(createdAt / 1000, 'unixepoch', 'localtime') as date,
                  COUNT(CASE state WHEN '${DataWorkerExecutionLogState.SUCCESS.name}' THEN 1 ELSE NULL END) as successCount,
                  COUNT(CASE state WHEN '${DataWorkerExecutionLogState.FAILURE.name}' THEN 1 ELSE NULL END) as failureCount
            FROM (SELECT * FROM WorkerExecutionLogEntity ORDER BY attendanceId)
            WHERE loggableWorker = '${loggableWorker.name}'
                AND attendanceId = '${attendanceId}'
            GROUP BY DATE((createdAt + 0.00) / 1000, 'unixepoch', 'localtime')
        """.trimIndent()

        return resultCountDao.getAll(
            SimpleSQLiteQuery(
                query,
                arrayOf()
            )
        ).flowOn(Dispatchers.IO)
    }
}