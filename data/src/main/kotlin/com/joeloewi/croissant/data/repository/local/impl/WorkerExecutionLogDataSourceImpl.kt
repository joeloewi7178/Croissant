/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.data.repository.local.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.db.SimpleSQLiteQuery
import com.joeloewi.croissant.data.database.dao.WorkerExecutionLogDao
import com.joeloewi.croissant.data.mapper.WorkerExecutionLogMapper
import com.joeloewi.croissant.data.mapper.WorkerExecutionLogWithStateMapper
import com.joeloewi.croissant.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.ResultCount
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkerExecutionLogDataSourceImpl @Inject constructor(
    private val workerExecutionLogDao: WorkerExecutionLogDao,
    private val workerExecutionLogMapper: WorkerExecutionLogMapper,
    private val workerExecutionLogWithStateMapper: WorkerExecutionLogWithStateMapper,
) : WorkerExecutionLogDataSource {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        withContext(Dispatchers.IO) {
            workerExecutionLogDao.insert(workerExecutionLogMapper.toData(workerExecutionLog))
        }

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        withContext(Dispatchers.IO) {
            workerExecutionLogDao.delete(*workerExecutionLogs.map {
                workerExecutionLogMapper.toData(
                    it
                )
            }.toTypedArray())
        }

    override suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int = withContext(Dispatchers.IO) {
        workerExecutionLogDao.deleteAll(attendanceId, loggableWorker)
    }

    override fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        dateString: String
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                workerExecutionLogDao.getByDatePaged(
                    attendanceId, loggableWorker, dateString
                )
            }
        ).flow
            .map { pagingData -> pagingData.map { workerExecutionLogWithStateMapper.toDomain(it) } }
            .flowOn(Dispatchers.IO)

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long> = workerExecutionLogDao.getCountByState(attendanceId, loggableWorker, state)
        .flowOn(Dispatchers.IO)

    override suspend fun hasExecutedAtLeastOnce(
        attendanceId: Long,
        gameName: HoYoLABGame,
        date: String
    ): Boolean = withContext(Dispatchers.IO) {
        val query = """
            SELECT
                COUNT(*)
            FROM 
                (
                    SELECT *
                    FROM WorkerExecutionLogEntity as log
                    LEFT OUTER JOIN
                    (
                        SELECT *
                        FROM (
                            SELECT
                                executionLogId, 
                                gameName
                            FROM FailureLogEntity
                            UNION
                            SELECT
                                executionLogId, 
                                gameName
                            FROM SuccessLogEntity
                        )
                    ) AS state
                    ON log.id = state.executionLogId
                )
            WHERE attendanceId = $attendanceId
            AND DATE(createdAt / 1000, 'unixepoch', 'localtime') = '${date}'
            AND gameName = '${gameName.name}'
        """.trimIndent()

        workerExecutionLogDao.getCountByDate(
            SimpleSQLiteQuery(
                query,
                arrayOf<ResultCount>()
            )
        ) > 0
    }
}