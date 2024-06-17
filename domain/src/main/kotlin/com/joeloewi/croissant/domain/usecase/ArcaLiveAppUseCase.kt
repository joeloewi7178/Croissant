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

import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.core.data.repository.ArcaLiveAppRepository
import javax.inject.Inject

sealed class ArcaLiveAppUseCase {
    class GetRedeemCode @Inject constructor(
        private val arcaLiveAppRepository: ArcaLiveAppRepository
    ) : ArcaLiveAppUseCase() {

        suspend operator fun invoke(
            game: HoYoLABGame
        ) = arcaLiveAppRepository.getRedeemCode(game)
    }
}