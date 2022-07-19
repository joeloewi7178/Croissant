package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.HonkaiImpact3rdCheckInRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class HonkaiImpact3rdCheckInUseCase {
    class AttendCheckInHonkaiImpact3rd @Inject constructor(
        private val honkaiImpact3rdCheckInRepository: HonkaiImpact3rdCheckInRepository
    ) : HonkaiImpact3rdCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            honkaiImpact3rdCheckInRepository.attendCheckInHonkaiImpact3rd(cookie = cookie)
    }
}