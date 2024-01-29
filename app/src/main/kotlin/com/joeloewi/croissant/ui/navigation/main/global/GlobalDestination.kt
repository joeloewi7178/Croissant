package com.joeloewi.croissant.ui.navigation.main.global

import androidx.compose.runtime.Immutable
import androidx.navigation.NavArgumentBuilder

@Immutable
sealed class GlobalDestination {
    abstract val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>>
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
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute = "firstLaunchScreen"
    }
}