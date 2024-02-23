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

package com.joeloewi.croissant.data.repository.local.impl

import android.app.Application
import com.joeloewi.croissant.data.datastore.settingsDataStore
import com.joeloewi.croissant.data.mapper.SettingsMapper
import com.joeloewi.croissant.data.repository.local.SettingsDataSource
import com.joeloewi.croissant.domain.entity.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsDataSourceImpl @Inject constructor(
    private val application: Application,
    private val settingsMapper: SettingsMapper
) : SettingsDataSource {
    override fun getSettings(): Flow<Settings> =
        application.settingsDataStore.data.map { settingsMapper.toDomain(it) }

    override suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings =
        withContext(Dispatchers.IO) {
            application.settingsDataStore.updateData {
                it.toBuilder().setDarkThemeEnabled(darkThemeEnabled).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }

    override suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings =
        withContext(Dispatchers.IO) {
            application.settingsDataStore.updateData {
                it.toBuilder().setIsFirstLaunch(isFirstLaunch).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }

    override suspend fun setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager: Boolean): Settings =
        withContext(Dispatchers.IO) {
            application.settingsDataStore.updateData {
                it.toBuilder().setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }
}