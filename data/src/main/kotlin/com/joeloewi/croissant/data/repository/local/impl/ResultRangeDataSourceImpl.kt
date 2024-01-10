package com.joeloewi.croissant.data.repository.local.impl

import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.data.database.dao.ResultRangeDao
import com.joeloewi.croissant.data.repository.local.ResultRangeDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.ResultRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultRangeDataSourceImpl @Inject constructor(
    private val resultRangeDao: ResultRangeDao
) : ResultRangeDataSource {
    override fun getStartToEnd(loggableWorker: LoggableWorker): Flow<ResultRange> {
        val query = """
            SELECT
                MIN(
                    IFNULL(
                        MIN(createdAt),
                        CAST (ROUND((julianday('now', 'localtime', 'start of month', 'start of day', 'utc') - 2440587.5) * 86400.0 * 1000) AS INTEGER)
                    ),
                    CAST (ROUND((julianday('now', 'localtime', 'start of month', 'start of day', 'utc') - 2440587.5) * 86400.0 * 1000) AS INTEGER)
                ) AS start,
                MAX(
                    IFNULL(
                        MAX(createdAt),
                        CAST (ROUND((julianday('now', 'localtime', '+1 month', 'start of month', 'start of day', '-0.001 second', 'utc') - 2440587.5) * 86400.0 * 1000) AS INTEGER)
                    ),
                    CAST (ROUND((julianday('now', 'localtime', '+1 month', 'start of month', 'start of day', '-0.001 second', 'utc') - 2440587.5) * 86400.0 * 1000) AS INTEGER)
                ) AS end
            FROM (
                SELECT * 
                FROM WorkerExecutionLogEntity 
                WHERE loggableWorker = "${loggableWorker.name}"
            )
        """.trimIndent()

        return resultRangeDao.getStartToEnd(
            SimpleSQLiteQuery(
                query,
                arrayOf<ResultRange>()
            )
        ).flowOn(Dispatchers.IO)
    }
}