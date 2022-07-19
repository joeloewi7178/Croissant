package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
sealed class SettingsUseCase {
    class GetSettings @Inject constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        operator fun invoke() = settingsRepository.getSettings()
    }

    class SetDarkThemeEnabled @Inject constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(darkThemeEnabled: Boolean) =
            settingsRepository.setDarkThemeEnabled(darkThemeEnabled)
    }

    class SetIsFirstLaunch @Inject constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(isFirstLaunch: Boolean) =
            settingsRepository.setIsFirstLaunch(isFirstLaunch)
    }

    class SetNotifyMigrateToAlarmManager @Inject constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(notifyMigrateToAlarmManager: Boolean) =
            settingsRepository.setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager)
    }
}
