package com.joeloewi.domain.usecase

import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.repository.WorkerExecutionLogRepository

sealed class WorkerExecutionLogUseCase {
    class Insert constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(workerExecutionLog: WorkerExecutionLog) =
            workerExecutionLogRepository.insert(workerExecutionLog)
    }

    class Delete constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(vararg workerExecutionLogs: WorkerExecutionLog) =
            workerExecutionLogRepository.delete(*workerExecutionLogs)
    }

    class DeleteAll constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker) =
            workerExecutionLogRepository.deleteAll(attendanceId, loggableWorker)
    }

    class GetAllPaged constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker) =
            workerExecutionLogRepository.getAllPaged(attendanceId, loggableWorker)
    }

    class GetCountByState constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            state: WorkerExecutionLogState
        ) = workerExecutionLogRepository.getCountByState(attendanceId, loggableWorker, state)
    }
}