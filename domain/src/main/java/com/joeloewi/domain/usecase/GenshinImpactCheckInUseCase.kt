package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.GenshinImpactCheckInRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class GenshinImpactCheckInUseCase {
    class AttendCheckInGenshinImpact @Inject constructor(
        private val genshinImpactCheckInRepository: GenshinImpactCheckInRepository
    ) : GenshinImpactCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            genshinImpactCheckInRepository.attendCheckInGenshinImpact(cookie = cookie)
    }
}