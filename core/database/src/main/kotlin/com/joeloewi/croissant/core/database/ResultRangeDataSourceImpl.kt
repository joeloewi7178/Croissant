package com.joeloewi.croissant.core.database

import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.core.data.model.ResultRange
import com.joeloewi.croissant.domain.common.LoggableWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ResultRangeDataSourceImpl @Inject constructor(
    private val resultRangeDao: com.joeloewi.croissant.core.database.dao.ResultRangeDao
) : com.joeloewi.croissant.core.database.ResultRangeDataSource {
    override fun getStartToEnd(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<com.joeloewi.croissant.core.data.model.ResultRange> {
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
                WHERE loggableWorker = '${loggableWorker.name}'
                    AND attendanceId = '${attendanceId}'
            )
        """.trimIndent()

        return resultRangeDao.getStartToEnd(
            SimpleSQLiteQuery(
                query,
                arrayOf<com.joeloewi.croissant.core.data.model.ResultRange>()
            )
        ).flowOn(Dispatchers.IO)
    }
}