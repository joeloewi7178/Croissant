package com.joeloewi.croissant.ui.navigation.main.redemptioncodes

import androidx.compose.runtime.Immutable
import androidx.navigation.NavArgumentBuilder

@Immutable
sealed class RedemptionCodesDestination {
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

    data object RedemptionCodesScreen : RedemptionCodesDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute: String = "redemptionCodesScreen"
    }
}
