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

package com.joeloewi.croissant.core.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.WorkerExecutionLog
import com.joeloewi.croissant.core.data.model.WorkerExecutionLogState
import com.joeloewi.croissant.core.data.model.asData
import com.joeloewi.croissant.core.data.model.relational.WorkerExecutionLogWithState
import com.joeloewi.croissant.core.data.model.relational.asExternalData
import com.joeloewi.croissant.core.database.WorkerExecutionLogDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkerExecutionLogRepositoryImpl @Inject constructor(
    private val workerExecutionLogDataSource: WorkerExecutionLogDataSource
) : WorkerExecutionLogRepository {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        workerExecutionLogDataSource.insert(workerExecutionLog.asData())

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        workerExecutionLogDataSource.delete(*workerExecutionLogs.map { it.asData() }.toTypedArray())

    override suspend fun deleteAll(attendanceId: Long, loggableWorker: LoggableWorker): Int =
        workerExecutionLogDataSource.deleteAll(attendanceId, loggableWorker.asData())

    override fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        localDate: String
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        workerExecutionLogDataSource.getByDatePaged(
            attendanceId,
            loggableWorker.asData(),
            localDate
        ).map { pagingData ->
            pagingData.map { it.asExternalData() }
        }

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long> =
        workerExecutionLogDataSource.getCountByState(
            attendanceId,
            loggableWorker.asData(),
            state.asData()
        )

    override suspend fun hasExecutedAtLeastOnce(
        attendanceId: Long,
        gameName: HoYoLABGame,
        timestamp: Long
    ): Boolean =
        workerExecutionLogDataSource.hasExecutedAtLeastOnce(
            attendanceId,
            gameName.asData(),
            timestamp
        )
}