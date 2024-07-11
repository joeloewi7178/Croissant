package com.joeloewi.croissant.ui.navigation.main.settings

import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed class SettingsDestination {
    open val arguments: ImmutableList<NamedNavArgument> = persistentListOf()
    protected abstract val plainRoute: String
    val route: String by lazy {
        "${plainRoute}${
            arguments.map { it.name }.joinToString(
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
        override val plainRoute: String = "settingsScreen"
    }

    data object DeveloperInfoScreen : SettingsDestination() {
        override val plainRoute: String = "developerInfoScreen"
    }
}
