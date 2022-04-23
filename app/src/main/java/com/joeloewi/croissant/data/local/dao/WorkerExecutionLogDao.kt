package com.joeloewi.croissant.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.croissant.data.common.LoggableWorker
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.local.model.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long

    @Delete
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int

    @Transaction
    @Query("SELECT * FROM WorkerExecutionLog WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker ORDER BY createdAt DESC")
    fun getAllPaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): PagingSource<Int, WorkerExecutionLogWithState>

    @Transaction
    @Query("SELECT COUNT(*) FROM WorkerExecutionLog WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker AND state = :state")
    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>
}