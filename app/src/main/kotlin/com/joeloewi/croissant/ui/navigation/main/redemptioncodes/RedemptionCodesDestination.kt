package com.joeloewi.croissant.ui.navigation.main.redemptioncodes

import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
sealed class RedemptionCodesDestination {
    open val arguments: ImmutableList<NamedNavArgument> = persistentListOf()
    protected abstract val plainRoute: String
    val route: String
        get() = "${plainRoute}${
            arguments.map { it.name }.joinToString(
                separator = "/",
                prefix = if (arguments.isEmpty()) {
                    ""
                } else {
                    "/"
                }
            ) { "{$it}" }
        }"

    data object RedemptionCodesScreen : RedemptionCodesDestination() {
        override val plainRoute: String = "redemptionCodesScreen"
    }
}
