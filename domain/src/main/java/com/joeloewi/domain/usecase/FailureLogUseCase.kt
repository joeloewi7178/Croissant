package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.repository.FailureLogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class FailureLogUseCase {
    class Insert @Inject constructor(
        private val failureLogRepository: FailureLogRepository
    ) : FailureLogUseCase() {
        suspend operator fun invoke(failureLogInterface: FailureLog) =
            failureLogRepository.insert(failureLogInterface)
    }
}
