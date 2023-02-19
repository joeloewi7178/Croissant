/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.domain.usecase

import com.joeloewi.croissant.domain.repository.SettingsRepository
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
