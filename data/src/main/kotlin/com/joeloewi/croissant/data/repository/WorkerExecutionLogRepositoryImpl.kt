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

package com.joeloewi.croissant.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.croissant.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.entity.relational.WorkerExecutionLogWithState
import com.joeloewi.croissant.domain.repository.WorkerExecutionLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkerExecutionLogRepositoryImpl @Inject constructor(
    private val workerExecutionLogDataSource: WorkerExecutionLogDataSource
) : WorkerExecutionLogRepository {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        workerExecutionLogDataSource.insert(workerExecutionLog)

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        workerExecutionLogDataSource.delete(*workerExecutionLogs)

    override suspend fun deleteAll(attendanceId: Long, loggableWorker: LoggableWorker): Int =
        workerExecutionLogDataSource.deleteAll(attendanceId, loggableWorker)

    override fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        localDate: String
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        workerExecutionLogDataSource.getByDatePaged(attendanceId, loggableWorker, localDate)
            .map { pagingData ->
                pagingData.map { it }
            }

    override fun getCountByStateAndDate(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState,
        localDate: String
    ): Flow<Long> = workerExecutionLogDataSource.getCountByStateAndDate(
        attendanceId,
        loggableWorker,
        state,
        localDate
    )

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long> =
        workerExecutionLogDataSource.getCountByState(attendanceId, loggableWorker, state)

    override fun getStartToEnd(): Flow<Pair<Long, Long>> =
        workerExecutionLogDataSource.getStartToEnd()
}