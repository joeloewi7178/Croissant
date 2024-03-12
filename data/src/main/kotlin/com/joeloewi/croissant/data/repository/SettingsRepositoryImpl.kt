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

package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.local.SettingsDataSource
import com.joeloewi.croissant.domain.entity.Settings
import com.joeloewi.croissant.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> =
        settingsDataSource.getSettings()

    override suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings =
        settingsDataSource.setDarkThemeEnabled(darkThemeEnabled)

    override suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings =
        settingsDataSource.setIsFirstLaunch(isFirstLaunch)

    override suspend fun setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager: Boolean): Settings =
        settingsDataSource.setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager)
}