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

import androidx.paging.PagingData
import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.DataWorkerExecutionLogState
import com.joeloewi.croissant.core.database.model.LogCountPerTypeAndStateEntity
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import com.joeloewi.croissant.core.database.model.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.croissant.core.model.DataHoYoLABGame
import kotlinx.coroutines.flow.Flow

interface WorkerExecutionLogDataSource {
    suspend fun insert(workerExecutionLog: WorkerExecutionLogEntity): Long
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLogEntity): Int
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Int

    fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        dateString: String
    ): Flow<PagingData<WorkerExecutionLogWithStateEntity>>

    fun getCountByState(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        state: DataWorkerExecutionLogState
    ): Flow<Long>

    suspend fun hasExecutedAtLeastOnce(
        attendanceId: Long,
        gameName: DataHoYoLABGame,
        timestamp: Long
    ): Boolean

    fun getLogCountPerTypeAndState(attendanceId: Long): Flow<LogCountPerTypeAndStateEntity>
}