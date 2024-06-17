/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.core.data.model.ResinStatusWidget
import com.joeloewi.croissant.core.data.repository.ResinStatusWidgetRepository
import javax.inject.Inject

sealed class ResinStatusWidgetUseCase {
    class GetAll @Inject constructor(
        private val resinStatusWidgetRepository: ResinStatusWidgetRepository
    ) : ResinStatusWidgetUseCase() {
        suspend operator fun invoke() = resinStatusWidgetRepository.getAll()
    }

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
