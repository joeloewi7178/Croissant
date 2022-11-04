package com.joeloewi.domain.usecase

import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.repository.WorkerExecutionLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class WorkerExecutionLogUseCase {
    class Insert @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(workerExecutionLog: WorkerExecutionLog) =
            workerExecutionLogRepository.insert(workerExecutionLog)
    }

    class Delete @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(vararg workerExecutionLogs: WorkerExecutionLog) =
            workerExecutionLogRepository.delete(*workerExecutionLogs)
    }

    class DeleteAll @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        suspend operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker) =
            workerExecutionLogRepository.deleteAll(attendanceId, loggableWorker)
    }

    class GetByDatePaged @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker, localDate: String) =
            workerExecutionLogRepository.getByDatePaged(attendanceId, loggableWorker, localDate)
    }

    class GetCountByStateAndDate @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            state: WorkerExecutionLogState,
            localDate: String,
        ) = workerExecutionLogRepository.getCountByStateAndDate(
            attendanceId,
            loggableWorker,
            state,
            localDate
        )
    }

    class GetCountByState @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) {
        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            state: WorkerExecutionLogState
        ) = workerExecutionLogRepository.getCountByState(attendanceId, loggableWorker, state)
    }
}