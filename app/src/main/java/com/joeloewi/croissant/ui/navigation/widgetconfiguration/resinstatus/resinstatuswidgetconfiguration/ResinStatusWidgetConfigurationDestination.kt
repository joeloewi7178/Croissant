package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration

import androidx.navigation.NavType

sealed class ResinStatusWidgetConfigurationDestination {
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

    object LoadingScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "loadingScreen"
    }

    class CreateResinStatusWidgetScreen : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        )

        override val plainRoute: String = "createResinStatusWidgetScreen"

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    class ResinStatusWidgetDetailScreen : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        )

        override val plainRoute: String = "resinStatusWidgetDetailScreen"

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }
}
