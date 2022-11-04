package com.joeloewi.domain.repository

import androidx.paging.PagingData
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.flow.Flow

interface WorkerExecutionLogRepository {
    suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int

    fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        localDate: String
    ): Flow<PagingData<WorkerExecutionLogWithState>>

    fun getCountByStateAndDate(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState,
        localDate: String,
    ): Flow<Long>

    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>
}