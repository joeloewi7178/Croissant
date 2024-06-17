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

package com.joeloewi.croissant.core.database

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.joeloewi.croissant.core.database.dao.WorkerExecutionLogDao
import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.DataWorkerExecutionLogState
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import com.joeloewi.croissant.core.database.model.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.croissant.core.model.DataHoYoLABGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkerExecutionLogDataSourceImpl @Inject constructor(
    private val workerExecutionLogDao: WorkerExecutionLogDao,
) : WorkerExecutionLogDataSource {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLogEntity): Long =
        withContext(Dispatchers.IO) {
            workerExecutionLogDao.insert(workerExecutionLog)
        }

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLogEntity): Int =
        withContext(Dispatchers.IO) {
            workerExecutionLogDao.delete(*workerExecutionLogs)
        }

    override suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Int = withContext(Dispatchers.IO) {
        workerExecutionLogDao.deleteAll(attendanceId, loggableWorker)
    }

    override fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        dateString: String
    ): Flow<PagingData<WorkerExecutionLogWithStateEntity>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                workerExecutionLogDao.getByDatePaged(
                    attendanceId, loggableWorker, dateString
                )
            }
        ).flow.flowOn(Dispatchers.IO)

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        state: DataWorkerExecutionLogState
    ): Flow<Long> = workerExecutionLogDao.getCountByState(attendanceId, loggableWorker, state)
        .flowOn(Dispatchers.IO)

    override suspend fun hasExecutedAtLeastOnce(
        attendanceId: Long,
        gameName: DataHoYoLABGame,
        timestamp: Long
    ): Boolean = withContext(Dispatchers.IO) {
        workerExecutionLogDao.getCountByDate(attendanceId, timestamp, gameName) > 0
    }
}