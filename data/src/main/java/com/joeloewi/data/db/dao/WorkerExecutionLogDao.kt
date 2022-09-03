package com.joeloewi.data.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.joeloewi.data.entity.local.WorkerExecutionLogEntity
import com.joeloewi.data.entity.local.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerExecutionLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workerExecutionLogEntity: WorkerExecutionLogEntity): Long

    @Delete
    suspend fun delete(vararg workerExecutionLogEntities: WorkerExecutionLogEntity): Int

    @Transaction
    @Query("DELETE FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker")
    fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int

    @Transaction
    @Query("SELECT * FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker ORDER BY createdAt DESC")
    fun getAllPaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): PagingSource<Int, WorkerExecutionLogWithStateEntity>

    @Transaction
    @Query("SELECT COUNT(*) FROM WorkerExecutionLogEntity WHERE attendanceId = :attendanceId AND loggableWorker = :loggableWorker AND state = :state")
    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>
}