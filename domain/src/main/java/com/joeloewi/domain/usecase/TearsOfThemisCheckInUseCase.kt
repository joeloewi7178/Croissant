package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.TearsOfThemisCheckInRepository

sealed class TearsOfThemisCheckInUseCase {
    class AttendCheckInTearsOfThemis constructor(
        private val tearsOfThemisCheckInRepository: TearsOfThemisCheckInRepository
    ) : TearsOfThemisCheckInUseCase() {
        suspend operator fun invoke(cookie: String) =
            tearsOfThemisCheckInRepository.attendCheckInTearsOfThemis(cookie = cookie)
    }
}
