package com.joeloewi.data.repository.local.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.joeloewi.data.db.dao.WorkerExecutionLogDao
import com.joeloewi.data.entity.WorkerExecutionLogEntity
import com.joeloewi.data.entity.relational.WorkerExecutionLogWithStateEntity
import com.joeloewi.data.mapper.WorkerExecutionLogMapper
import com.joeloewi.data.mapper.WorkerExecutionLogWithStateMapper
import com.joeloewi.data.repository.local.WorkerExecutionLogDataSource
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkerExecutionLogDataSourceImpl @Inject constructor(
    private val workerExecutionLogDao: WorkerExecutionLogDao,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val workerExecutionLogMapper: WorkerExecutionLogMapper,
    private val workerExecutionLogWithStateMapper: WorkerExecutionLogWithStateMapper,
) : WorkerExecutionLogDataSource {

    override suspend fun insert(workerExecutionLog: WorkerExecutionLog): Long =
        withContext(coroutineDispatcher) {
            workerExecutionLogDao.insert(workerExecutionLogMapper.toData(workerExecutionLog))
        }

    override suspend fun delete(vararg workerExecutionLogs: WorkerExecutionLog): Int =
        withContext(coroutineDispatcher) {
            workerExecutionLogDao.delete(*workerExecutionLogs.map {
                workerExecutionLogMapper.toData(
                    it
                )
            }.toTypedArray())
        }

    override suspend fun deleteAll(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Int = withContext(coroutineDispatcher) {
        workerExecutionLogDao.deleteAll(attendanceId, loggableWorker)
    }

    override fun getAllPaged(
        attendanceId: Long,
        loggableWorker: LoggableWorker
    ): Flow<PagingData<WorkerExecutionLogWithState>> =
        Pager(
            config = PagingConfig(
                pageSize = 8
            ),
            pagingSourceFactory = {
                workerExecutionLogDao.getAllPaged(
                    attendanceId, loggableWorker
                )
            }
        ).flow
            .map { pagingData -> pagingData.map { workerExecutionLogWithStateMapper.toDomain(it) } }
            .flowOn(coroutineDispatcher)

    override fun getCountByState(
        attendanceId: Long,
        loggableWorker: LoggableWorker,
        state: com.joeloewi.domain.common.WorkerExecutionLogState
    ): Flow<Long> = workerExecutionLogDao.getCountByState(attendanceId, loggableWorker, state)
        .flowOn(coroutineDispatcher)
}