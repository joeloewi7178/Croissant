package com.joeloewi.data.repository

import com.joeloewi.data.repository.local.SettingsDataSource
import com.joeloewi.domain.entity.Settings
import com.joeloewi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) : SettingsRepository {

    override fun getSettings(): Flow<Settings> = settingsDataSource.getSettings()

    override suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings =
        settingsDataSource.setDarkThemeEnabled(darkThemeEnabled)

    override suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings =
        settingsDataSource.setIsFirstLaunch(isFirstLaunch)
}