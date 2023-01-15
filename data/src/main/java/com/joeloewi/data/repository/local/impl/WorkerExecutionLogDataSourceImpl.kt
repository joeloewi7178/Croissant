package com.joeloewi.data.repository.local.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.data.db.dao.WorkerExecutionLogDao
import com.joeloewi.data.mapper.WorkerExecutionLogMapper
import com.joeloewi.data.mapper.WorkerExecutionLogWithStateMapper
import com.joeloewi.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkerExecutionLogDataSourceImpl @Inject constructor(
    private val workerExecutionLogDao: WorkerExecutionLogDao,
    private val workerExecutionLogMapper: WorkerExecutionLogMapper,
    private val workerExecutionLogWithStateMapper: WorkerExecutionLogWithStateMapper,
) : WorkerExecutionLogDataSource {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        workerExecutionLogDao.insert(workerExecutionLogMapper.toData(workerExecutionLog))

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        workerExecutionLogDao.delete(*workerExecutionLogs.map {
            workerExecutionLogMapper.toData(
                it
            )
        }.toTypedArray())

    override suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int = workerExecutionLogDao.deleteAll(attendanceId, loggableWorker)

    override fun getByDatePaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        dateString: String
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                workerExecutionLogDao.getByDatePaged(
                    attendanceId, loggableWorker, dateString
                )
            }
        ).flow
            .map { pagingData -> pagingData.map { workerExecutionLogWithStateMapper.toDomain(it) } }

    override fun getCountByStateAndDate(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState,
        dateString: String,
    ): Flow<Long> = workerExecutionLogDao.getCountByStateAndDate(
        attendanceId,
        loggableWorker,
        state,
        dateString
    )

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: WorkerExecutionLogState
    ): Flow<Long> = workerExecutionLogDao.getCountByState(attendanceId, loggableWorker, state)
}