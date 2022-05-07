package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.HoYoLABRepository
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

    class AttendCheckInGenshinImpact @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInGenshinImpact(cookie)
    }

    class AttendCheckInHonkaiImpact3rd @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInHonkaiImpact3rd(cookie)
    }

    class AttendCheckInTearsOfThemis @Inject constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInTearsOfThemis(cookie)
    }
}
