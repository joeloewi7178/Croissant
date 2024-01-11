package com.joeloewi.croissant.ui.navigation.main.global

import androidx.navigation.NavArgumentBuilder

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

    data object EmptyScreen : GlobalDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = emptyList()
        override val plainRoute: String = "EmptyScreen"
    }

    data object FirstLaunchScreen : GlobalDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute = "firstLaunchScreen"
    }
}