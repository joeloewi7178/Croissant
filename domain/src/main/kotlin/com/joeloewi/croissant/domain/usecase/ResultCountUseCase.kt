package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.repository.ResultCountRepository
import javax.inject.Inject

sealed class ResultCountUseCase {

    class GetAll @Inject constructor(
        private val resultCountRepository: ResultCountRepository
    ) : ResultCountUseCase() {

        operator fun invoke(loggableWorker: LoggableWorker) =
            resultCountRepository.getAll(loggableWorker)
    }
}