package com.joeloewi.croissant.ui.navigation.main.settings

import androidx.compose.runtime.Immutable
import androidx.navigation.NavArgumentBuilder

@Immutable
sealed class SettingsDestination {
    abstract val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>>
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

    data object SettingsScreen : SettingsDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute: String = "settingsScreen"
    }

    data object DeveloperInfoScreen : SettingsDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute: String = "developerInfoScreen"
    }
}
