package com.joeloewi.croissant.ui.navigation.main.redemptioncodes

import androidx.navigation.NavType

sealed class RedemptionCodesDestination {
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

    object RedemptionCodesScreen : RedemptionCodesDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "redemptionCodesScreen"
    }
}
