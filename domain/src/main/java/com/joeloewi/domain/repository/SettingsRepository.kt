package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun setDarkThemeEnabled(darkThemeEnabled: Boolean): Settings
    suspend fun setIsFirstLaunch(isFirstLaunch: Boolean): Settings
}