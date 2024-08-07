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

import com.joeloewi.croissant.domain.repository.CheckInRepository
import javax.inject.Inject

sealed class CheckInUseCase {

    class AttendCheckInTearsOfThemis @Inject constructor(
        private val checkInRepository: CheckInRepository
    ) : CheckInUseCase() {
        suspend operator fun invoke(
            actId: String = "e202202281857121",
            cookie: String
        ) = checkInRepository.attend(actId, cookie)
    }

    class AttendCheckInHonkaiStarRailUseCase @Inject constructor(
        private val checkInRepository: CheckInRepository
    ) : CheckInUseCase() {
        suspend operator fun invoke(
            actId: String = "e202303301540311",
            cookie: String
        ) = checkInRepository.attend(actId, cookie)
    }

    class AttendCheckInHonkaiImpact3rd @Inject constructor(
        private val checkInRepository: CheckInRepository
    ) : CheckInUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = checkInRepository.attendCheckInHonkaiImpact3rd(cookie)
    }

    class AttendCheckInGenshinImpact @Inject constructor(
        private val checkInRepository: CheckInRepository
    ) : CheckInUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = checkInRepository.attendCheckInGenshinImpact(cookie)
    }

    class AttendCheckInZenlessZoneZeroUseCase @Inject constructor(
        private val checkInRepository: CheckInRepository
    ) : CheckInUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = checkInRepository.attendCheckInZenlessZoneZero(cookie)
    }
}