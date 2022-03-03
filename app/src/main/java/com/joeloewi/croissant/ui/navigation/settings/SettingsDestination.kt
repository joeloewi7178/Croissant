package com.joeloewi.croissant.ui.navigation.settings

sealed class SettingsDestination(val route: String) {
    object SettingsScreen : SettingsDestination(route = "settingsScreen")
}
