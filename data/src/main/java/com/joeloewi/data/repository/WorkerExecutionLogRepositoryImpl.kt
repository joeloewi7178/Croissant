package com.joeloewi.data.repository

import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import com.joeloewi.domain.repository.WorkerExecutionLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkerExecutionLogRepositoryImpl @Inject constructor(
    private val workerExecutionLogDataSource: WorkerExecutionLogDataSource
) : WorkerExecutionLogRepository {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        workerExecutionLogDataSource.insert(workerExecutionLog)

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        workerExecutionLogDataSource.delete(*workerExecutionLogs)

    override suspend fun deleteAll(attendanceId: Long, loggableWorker: LoggableWorker): Int =
        workerExecutionLogDataSource.deleteAll(attendanceId, loggableWorker)

    override fun getAllPaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        workerExecutionLogDataSource.getAllPaged(attendanceId, loggableWorker).map { pagingData ->
            pagingData.map { it }
        }

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long> =
        workerExecutionLogDataSource.getCountByState(attendanceId, loggableWorker, state)
}