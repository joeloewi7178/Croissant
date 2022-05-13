package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.HoYoLABRepository

sealed class HoYoLABUseCase {
    class GetUserFullInfo constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String) = hoYoLABRepository.getUserFullInfo(cookie)
    }

    class GetGameRecordCard constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String, uid: Long) =
            hoYoLABRepository.getGameRecordCard(cookie, uid)
    }

    class GetGenshinDailyNote constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(cookie: String, roleId: Long, server: String) =
            hoYoLABRepository.getGenshinDailyNote(cookie, roleId, server)
    }

    class ChangeDataSwitch constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String,
            switchId: Int,
            isPublic: Boolean,
            gameId: Int
        ) = hoYoLABRepository.changeDataSwitch(cookie, switchId, isPublic, gameId)
    }

    class AttendCheckInGenshinImpact constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInGenshinImpact(cookie)
    }

    class AttendCheckInHonkaiImpact3rd constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInHonkaiImpact3rd(cookie)
    }

    class AttendCheckInTearsOfThemis constructor(
        private val hoYoLABRepository: HoYoLABRepository
    ) : HoYoLABUseCase() {
        suspend operator fun invoke(
            cookie: String
        ) = hoYoLABRepository.attendCheckInTearsOfThemis(cookie)
    }
}
