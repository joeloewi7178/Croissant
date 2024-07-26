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

package com.joeloewi.croissant.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.joeloewi.croissant.core.database.model.DataLoggableWorker
import com.joeloewi.croissant.core.database.model.DataWorkerExecutionLogState
import com.joeloewi.croissant.core.database.model.LogCountPerTypeAndStateEntity
import com.joeloewi.croissant.core.database.model.WorkerExecutionLogEntity
import com.joeloewi.croissant.core.database.model.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.croissant.core.model.DataHoYoLABGame
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workerExecutionLogEntity: WorkerExecutionLogEntity): Long

    @Delete
    suspend fun delete(vararg workerExecutionLogEntities: WorkerExecutionLogEntity): Int

    @Query(
        """
            DELETE 
            FROM WorkerExecutionLogEntity 
            WHERE 
                attendanceId = :attendanceId 
                AND loggableWorker = :loggableWorker
        """
    )
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker
    ): Int

    @Transaction
    @Query(
        """
            SELECT * 
            FROM WorkerExecutionLogEntity 
            WHERE 
                attendanceId = :attendanceId 
                AND loggableWorker = :loggableWorker 
                AND DATE(createdAt / 1000, 'unixepoch', 'localtime') = :localDate 
            ORDER BY createdAt DESC
        """
    )
    fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        localDate: String,
    ): PagingSource<Int, WorkerExecutionLogWithStateEntity>

    @Query(
        """
            SELECT COUNT(*) 
            FROM WorkerExecutionLogEntity 
            WHERE 
                attendanceId = :attendanceId 
                AND loggableWorker = :loggableWorker 
                AND state = :state
        """
    )
    fun getCountByState(
        attendanceId: Long,
        loggableWorker: DataLoggableWorker,
        state: DataWorkerExecutionLogState
    ): Flow<Long>

    @Query(
        """
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
            WHERE attendanceId = :attendanceId
            AND createdAt >= :timestamp
            AND gameName = :gameName
        """
    )
    suspend fun getCountByDate(
        attendanceId: Long,
        timestamp: Long,
        gameName: DataHoYoLABGame
    ): Long

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT
            attendanceId,
            SUM(CASE WHEN loggableWorker = 'ATTEND_CHECK_IN_EVENT' AND state = 'SUCCESS' THEN 1 ELSE 0 END) AS checkInSuccess,
            SUM(CASE WHEN loggableWorker = 'ATTEND_CHECK_IN_EVENT' AND state = 'FAILURE' THEN 1 ELSE 0 END) AS checkInFailure,
            SUM(CASE WHEN loggableWorker = 'CHECK_SESSION' AND state = 'SUCCESS' THEN 1 ELSE 0 END) AS checkSessionSuccess,
            SUM(CASE WHEN loggableWorker = 'CHECK_SESSION' AND state = 'FAILURE' THEN 1 ELSE 0 END) AS checkSessionFailure
        FROM
            WorkerExecutionLogEntity
        WHERE
            attendanceId = :attendanceId
        GROUP BY
            attendanceId
        """
    )
    fun getLogCountPerTypeAndState(attendanceId: Long): Flow<LogCountPerTypeAndStateEntity>
}