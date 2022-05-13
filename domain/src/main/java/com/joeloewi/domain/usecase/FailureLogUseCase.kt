package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.repository.FailureLogRepository

sealed class FailureLogUseCase {
    class Insert constructor(
        private val failureLogRepository: FailureLogRepository
    ) : FailureLogUseCase() {
        suspend operator fun invoke(failureLogInterface: FailureLog) =
            failureLogRepository.insert(failureLogInterface)
    }
}
