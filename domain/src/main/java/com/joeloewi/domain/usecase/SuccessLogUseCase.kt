package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.repository.SuccessLogRepository

sealed class SuccessLogUseCase {
    class Insert constructor(
        private val successLogRepository: SuccessLogRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(successLog: SuccessLog) =
            successLogRepository.insert(successLog)
    }
}
