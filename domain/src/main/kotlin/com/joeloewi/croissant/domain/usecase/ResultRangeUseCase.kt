package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.repository.ResultRangeRepository
import javax.inject.Inject

sealed class ResultRangeUseCase {

    class GetStartToEnd @Inject constructor(
        private val resultRangeRepository: ResultRangeRepository
    ) : ResultRangeUseCase() {
        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker
        ) = resultRangeRepository.getStartToEnd(attendanceId, loggableWorker)
    }
}