package com.joeloewi.croissant.ui.navigation.main.settings

sealed class SettingsDestination(val route: String) {
    object SettingsScreen : SettingsDestination(route = "settingsScreen")
    object DeveloperInfoScreen : SettingsDestination(route = "developerInfoScreen")
}
