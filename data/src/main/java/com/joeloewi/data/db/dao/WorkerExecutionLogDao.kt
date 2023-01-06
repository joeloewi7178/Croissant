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