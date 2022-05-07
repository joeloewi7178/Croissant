package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.repository.SuccessLogRepository
import javax.inject.Inject

sealed class SuccessLogUseCase {
    class Insert @Inject constructor(
        private val successLogRepository: SuccessLogRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(successLog: SuccessLog) =
            successLogRepository.insert(successLog)
    }
}
