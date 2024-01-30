package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.repository.SystemRepository
import javax.inject.Inject

sealed class SystemUseCase {

    class Is24HourFormat @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        operator fun invoke() = systemRepository.is24HourFormat()
    }

    class IsDeviceRooted @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        suspend operator fun invoke() = systemRepository.isDeviceRooted()
    }

    class IsUnusedAppRestrictionEnabled @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        suspend operator fun invoke() = systemRepository.isUnusedAppRestrictionEnabled()
    }

    class RemoveAllCookies @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        suspend operator fun invoke() = systemRepository.removeAllCookies()
    }

    class IsNetworkAvailable @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        suspend operator fun invoke() = systemRepository.isNetworkAvailable()
    }

    class IsNetworkVpn @Inject constructor(
        private val systemRepository: SystemRepository
    ) : SystemUseCase() {
        suspend operator fun invoke() = systemRepository.isNetworkVpn()
    }
}