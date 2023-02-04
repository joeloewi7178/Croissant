package com.joeloewi.croissant.domain.entity

data class Settings(
    val darkThemeEnabled: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val notifyMigrateToAlarmManager: Boolean = true
)
