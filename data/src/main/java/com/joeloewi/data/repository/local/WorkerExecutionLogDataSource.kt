package com.joeloewi.data.repository.local

import androidx.paging.PagingData
import com.joeloewi.data.entity.WorkerExecutionLogEntity
import com.joeloewi.data.entity.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.flow.Flow

interface WorkerExecutionLogDataSource {
    suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long
    suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int
    suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int

    fun getAllPaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<PagingData<WorkerExecutionLogWithState>>

    fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long>
}