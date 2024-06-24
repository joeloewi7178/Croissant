package com.joeloewi.croissant.core.data.model

data class Settings(
    val darkThemeEnabled: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val notifyMigrateToAlarmManager: Boolean = true
)

fun com.joeloewi.croissant.core.datastore.Settings.asExternalData(): Settings = with(this) {
    Settings(
        darkThemeEnabled = darkThemeEnabled,
        isFirstLaunch = isFirstLaunch,
        notifyMigrateToAlarmManager = notifyMigrateToAlarmManager
    )
}

fun Settings.asData(): com.joeloewi.croissant.core.datastore.Settings = with(this) {
    com.joeloewi.croissant.core.datastore.Settings.newBuilder()
        .setDarkThemeEnabled(darkThemeEnabled)
        .setIsFirstLaunch(isFirstLaunch)
        .setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager)
        .build()
}
