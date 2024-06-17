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

import com.joeloewi.croissant.core.data.repository.HoYoLABRepository
import javax.inject.Inject

sealed class HoYoLABUseCase {
    class GetUserFullInfo @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String) = hoYoLABRepository.getUserFullInfo(cookie)
    }

    class GetGameRecordCard @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String, uid: Long) =
            hoYoLABRepository.getGameRecordCard(cookie, uid)
    }

    class GetGenshinDailyNote @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String, roleId: Long, server: String) =
            hoYoLABRepository.getGenshinDailyNote(cookie, roleId, server)
    }

    class ChangeDataSwitch @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String,
            switchId: Int,
            isPublic: Boolean,
            gameId: Int
        ) = hoYoLABRepository.changeDataSwitch(cookie, switchId, isPublic, gameId)
    }
}
