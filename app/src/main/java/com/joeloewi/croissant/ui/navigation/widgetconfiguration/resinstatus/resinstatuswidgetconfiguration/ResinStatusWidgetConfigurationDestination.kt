package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration

import androidx.navigation.NavType

sealed class ResinStatusWidgetConfigurationDestination(
    val route: String = ""
) {
    object LoadingScreen : ResinStatusWidgetConfigurationDestination(
        route = "loadingScreen"
    )

    data class CreateResinStatusWidgetScreen(
        val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        ),
        val plainRoute: String = "createResinStatusWidgetScreen"
    ) : ResinStatusWidgetConfigurationDestination(
        route = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = "/"
            ) { "{$it}" }
        }"
    ) {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    data class ResinStatusWidgetDetailScreen(
        val arguments: List<Pair<String, NavType<*>>> = listOf(
            APP_WIDGET_ID to NavType.IntType
        ),
        val plainRoute: String = "resinStatusWidgetDetailScreen"
    ) : ResinStatusWidgetConfigurationDestination(
        route = "${plainRoute}${
            arguments.map { it.first }.joinToString(
                separator = "/",
                prefix = "/"
            ) { "{$it}" }
        }"
    ) {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }
}
