package com.joeloewi.croissant.ui.navigation.main.global

import androidx.compose.runtime.Immutable
import androidx.navigation.NavArgumentBuilder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed class GlobalDestination {
    abstract val arguments: ImmutableList<Pair<String, NavArgumentBuilder.() -> Unit>>
    abstract val plainRoute: String
    open val route: String
        get() = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = if (arguments.isEmpty()) {
                    ""
                } else {
                    "/"
                }
            ) { "{$it}" }
        }"

    data object FirstLaunchScreen : GlobalDestination() {
        override val arguments: ImmutableList<Pair<String, NavArgumentBuilder.() -> Unit>> =
            persistentListOf()
        override val plainRoute = "firstLaunchScreen"
    }
}