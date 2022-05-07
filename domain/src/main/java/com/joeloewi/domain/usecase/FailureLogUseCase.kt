package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.repository.FailureLogRepository
import javax.inject.Inject

sealed class FailureLogUseCase {
    class Insert @Inject constructor(
        private val failureLogRepository: FailureLogRepository
    ) : FailureLogUseCase() {
        suspend operator fun invoke(failureLogInterface: FailureLog) =
            failureLogRepository.insert(failureLogInterface)
    }
}
