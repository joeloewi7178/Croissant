package com.joeloewi.data.repository.local

import com.joeloewi.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {
    fun getSettings(): Flow<Settings>
    suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings
    suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings
}