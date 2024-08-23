/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.domain

import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.WorkerExecutionLog
import com.joeloewi.croissant.core.data.model.WorkerExecutionLogState
import com.joeloewi.croissant.core.data.repository.WorkerExecutionLogRepository
import javax.inject.Inject

sealed class WorkerExecutionLogUseCase {
    class Insert @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {
        suspend operator fun invoke(workerExecutionLog: WorkerExecutionLog) =
            workerExecutionLogRepository.insert(workerExecutionLog)
    }

    class Delete @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {
        suspend operator fun invoke(vararg workerExecutionLogs: WorkerExecutionLog) =
            workerExecutionLogRepository.delete(*workerExecutionLogs)
    }

    class DeleteAll @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {
        suspend operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker) =
            workerExecutionLogRepository.deleteAll(attendanceId, loggableWorker)
    }

    class GetByDatePaged @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {
        operator fun invoke(attendanceId: Long, loggableWorker: LoggableWorker, localDate: String) =
            workerExecutionLogRepository.getByDatePaged(attendanceId, loggableWorker, localDate)
    }

    class GetCountByState @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {
        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker,
            state: WorkerExecutionLogState
        ) = workerExecutionLogRepository.getCountByState(attendanceId, loggableWorker, state)
    }

    class HasExecutedAtLeastOnce @Inject constructor(
        private val workerExecutionLogRepository: WorkerExecutionLogRepository
    ) : WorkerExecutionLogUseCase() {

        suspend operator fun invoke(
            attendanceId: Long,
            gameName: HoYoLABGame,
            timestamp: Long
        ) = workerExecutionLogRepository.hasExecutedAtLeastOnce(attendanceId, gameName, timestamp)
    }
}