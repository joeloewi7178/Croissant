package com.joeloewi.domain.entity

data class Settings(
    val darkThemeEnabled: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val notifyMigrateToAlarmManager: Boolean = true
)
