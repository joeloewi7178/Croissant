package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.repository.SystemRepository
import javax.inject.Inject

sealed class SystemUseCase {

    class Is24HourFormat @Inject constructor(
        private val systemRepository: SystemRepository
    ) {
        operator fun invoke() = systemRepository.is24HourFormat()
    }

    class IsDeviceRooted @Inject constructor(
        private val systemRepository: SystemRepository
    ) {
        suspend operator fun invoke() = systemRepository.isDeviceRooted()
    }
}