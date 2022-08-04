package com.joeloewi.croissant.ui.navigation.main.settings

import androidx.navigation.NavType

sealed class SettingsDestination {
    abstract val arguments: List<Pair<String, NavType<*>>>
    protected abstract val plainRoute: String
    val route: String by lazy {
        "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = if (arguments.isEmpty()) {
                    ""
                } else {
                    "/"
                }
            ) { "{$it}" }
        }"
    }

    object SettingsScreen : SettingsDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "settingsScreen"
    }

    object DeveloperInfoScreen : SettingsDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "developerInfoScreen"
    }
}
