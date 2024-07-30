package com.joeloewi.croissant.domain

import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.repository.ResultCountRepository
import javax.inject.Inject

sealed class ResultCountUseCase {

    class GetAll @Inject constructor(
        private val resultCountRepository: ResultCountRepository
    ) : ResultCountUseCase() {

        operator fun invoke(
            attendanceId: Long,
            loggableWorker: LoggableWorker
        ) = resultCountRepository.getAll(attendanceId, loggableWorker)
    }
}