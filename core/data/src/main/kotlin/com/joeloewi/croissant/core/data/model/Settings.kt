package com.joeloewi.croissant.core.data.model

data class Settings(
    val darkThemeEnabled: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val notifyMigrateToAlarmManager: Boolean = true
)

fun Settings.asExternalData(): com.joeloewi.croissant.core.data.model.Settings = with(this) {
    Settings(
        darkThemeEnabled = darkThemeEnabled,
        isFirstLaunch = isFirstLaunch,
        notifyMigrateToAlarmManager = notifyMigrateToAlarmManager
    )
}

fun com.joeloewi.croissant.core.data.model.Settings.asData(): Settings = with(this) {
    Settings.newBuilder()
        .setDarkThemeEnabled(darkThemeEnabled)
        .setIsFirstLaunch(isFirstLaunch)
        .setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager)
        .build()
}
