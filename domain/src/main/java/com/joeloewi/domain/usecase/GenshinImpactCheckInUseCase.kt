package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.GenshinImpactCheckInRepository

sealed class GenshinImpactCheckInUseCase {
    class AttendCheckInGenshinImpact constructor(
        private val genshinImpactCheckInRepository: GenshinImpactCheckInRepository
    ) : GenshinImpactCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            genshinImpactCheckInRepository.attendCheckInGenshinImpact(cookie = cookie)
    }
}