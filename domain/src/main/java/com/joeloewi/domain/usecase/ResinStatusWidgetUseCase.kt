package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.repository.ResinStatusWidgetRepository
import javax.inject.Inject

sealed class ResinStatusWidgetUseCase {
    class Insert @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.insert(resinStatusWidget)
    }

    class Delete @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(vararg resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.delete(*resinStatusWidget)
    }

    class Update @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.update(resinStatusWidget)
    }

    class GetOne @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(id: Long) =
            resinStatusWidgetRepository.getOne(id)
    }

    class DeleteByAppWidgetId @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(vararg appWidgetIds: Int) =
            resinStatusWidgetRepository.deleteByAppWidgetId(*appWidgetIds)
    }

    class GetOneByAppWidgetId @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(appWidgetId: Int) =
            resinStatusWidgetRepository.getOneByAppWidgetId(appWidgetId)
    }
}
