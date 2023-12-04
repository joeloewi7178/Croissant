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

package com.joeloewi.croissant.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.joeloewi.croissant.data.entity.local.WorkerExecutionLogEntity
import com.joeloewi.croissant.data.entity.local.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workerExecutionLogEntity: WorkerExecutionLogEntity): Long

    @Delete
    suspend fun delete(vararg workerExecutionLogEntities: WorkerExecutionLogEntity): Int

    @Transaction
    @Query("DELETE FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker")
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int

    @Transaction
    @Query("SELECT * FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker AND DATE(createdAt / 1000, 'unixepoch', 'localtime') = :localDate ORDER BY createdAt DESC")
    fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        localDate: String,
    ): PagingSource<Int, WorkerExecutionLogWithStateEntity>

    @Transaction
    @Query("SELECT COUNT(*) FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker AND state = :state AND DATE(createdAt / 1000, 'unixepoch', 'localtime') = :localDate")
    fun getCountByStateAndDate(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState,
        localDate: String,
    ): Flow<Long>

    @Transaction
    @Query("SELECT COUNT(*) FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker AND state = :state")
    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>
}