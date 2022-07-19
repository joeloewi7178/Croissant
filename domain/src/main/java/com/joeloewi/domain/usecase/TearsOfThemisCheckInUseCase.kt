package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.TearsOfThemisCheckInRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class TearsOfThemisCheckInUseCase {
    class AttendCheckInTearsOfThemis @Inject constructor(
        private val tearsOfThemisCheckInRepository: TearsOfThemisCheckInRepository
    ) : TearsOfThemisCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            tearsOfThemisCheckInRepository.attendCheckInTearsOfThemis(cookie = cookie)
    }
}
