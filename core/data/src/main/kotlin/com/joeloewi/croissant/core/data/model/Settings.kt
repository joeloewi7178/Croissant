package com.joeloewi.croissant.core.data.model

data class Settings(
    val darkThemeEnabled: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val notifyMigrateToAlarmManager: Boolean = true
)
