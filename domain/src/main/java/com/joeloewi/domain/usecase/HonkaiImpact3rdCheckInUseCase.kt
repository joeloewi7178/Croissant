package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.HonkaiImpact3rdCheckInRepository

sealed class HonkaiImpact3rdCheckInUseCase {
    class AttendCheckInHonkaiImpact3rd constructor(
        private val honkaiImpact3rdCheckInRepository: HonkaiImpact3rdCheckInRepository
    ) : HonkaiImpact3rdCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            honkaiImpact3rdCheckInRepository.attendCheckInHonkaiImpact3rd(cookie = cookie)
    }
}