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

package com.joeloewi.croissant.data.repository.local

import androidx.paging.PagingData
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.flow.Flow

interface WorkerExecutionLogDataSource {
    suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int

    fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        dateString: String
    ): Flow<PagingData<WorkerExecutionLogWithState>>

    fun getCountByStateAndDate(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState,
        dateString: String,
    ): Flow<Long>

    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>

    fun getStartToEnd(): Flow<Pair<Long, Long>>
}