package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.SettingsRepository

sealed class SettingsUseCase {
    class GetSettings constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        operator fun invoke() = settingsRepository.getSettings()
    }

    class SetDarkThemeEnabled constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(darkThemeEnabled: Boolean) =
            settingsRepository.setDarkThemeEnabled(darkThemeEnabled)
    }

    class SetIsFirstLaunch constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(isFirstLaunch: Boolean) =
            settingsRepository.setIsFirstLaunch(isFirstLaunch)
    }

    class SetNotifyMigrateToAlarmManager constructor(
        private val settingsRepository: SettingsRepository
    ) : SettingsUseCase() {
        suspend operator fun invoke(notifyMigrateToAlarmManager: Boolean) =
            settingsRepository.setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager)
    }
}
