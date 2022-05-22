package com.joeloewi.data.repository.local.impl

import android.app.Application
import com.joeloewi.data.datastore.settingsDataStore
import com.joeloewi.data.mapper.SettingsMapper
import com.joeloewi.data.repository.local.SettingsDataSource
import com.joeloewi.domain.entity.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsDateSourceImpl @Inject constructor(
    private val application: Application,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val settingsMapper: SettingsMapper
) : SettingsDataSource {
    override fun getSettings(): Flow<Settings> =
        application.settingsDataStore.data.map { settingsMapper.toDomain(it) }
            .flowOn(coroutineDispatcher)

    override suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings =
        withContext(coroutineDispatcher) {
            application.settingsDataStore.updateData {
                it.toBuilder().setDarkThemeEnabled(darkThemeEnabled).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }

    override suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings =
        withContext(coroutineDispatcher) {
            application.settingsDataStore.updateData {
                it.toBuilder().setIsFirstLaunch(isFirstLaunch).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }

    override suspend fun setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager: Boolean): Settings =
        withContext(coroutineDispatcher) {
            application.settingsDataStore.updateData {
                it.toBuilder().setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager).build()
            }.let {
                settingsMapper.toDomain(it)
            }
        }
}