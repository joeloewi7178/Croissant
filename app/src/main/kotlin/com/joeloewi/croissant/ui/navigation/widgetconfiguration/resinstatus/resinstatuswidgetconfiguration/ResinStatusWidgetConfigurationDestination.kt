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

    object EmptyScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "emptyScreen"
    }

    class LoadingScreen(
        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        ),
        override val plainRoute: String = "loadingScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    class CreateResinStatusWidgetScreen(
        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        ),
        override val plainRoute: String = "createResinStatusWidgetScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    class ResinStatusWidgetDetailScreen(
        override val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        ),
        override val plainRoute: String = "resinStatusWidgetDetailScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    object LoginHoYoLABScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<Pair<String, NavType<*>>> = listOf()
        override val plainRoute: String = "loginHoYoLABScreen"
    }
}
