package com.joeloewi.domain.usecase

import com.joeloewi.domain.entity.ResinStatusWidget
import com.joeloewi.domain.repository.ResinStatusWidgetRepository

sealed class ResinStatusWidgetUseCase {
    class Insert constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.insert(resinStatusWidget)
    }

    class Delete constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(vararg resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.delete(*resinStatusWidget)
    }

    class Update constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(resinStatusWidget: ResinStatusWidget) =
            resinStatusWidgetRepository.update(resinStatusWidget)
    }

    class GetOne constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(id: Long) =
            resinStatusWidgetRepository.getOne(id)
    }

    class DeleteByAppWidgetId constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(vararg appWidgetIds: Int) =
            resinStatusWidgetRepository.deleteByAppWidgetId(*appWidgetIds)
    }

    class GetOneByAppWidgetId constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke(appWidgetId: Int) =
            resinStatusWidgetRepository.getOneByAppWidgetId(appWidgetId)
    }
}
