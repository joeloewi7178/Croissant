package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration

import android.appwidget.AppWidgetManager
import androidx.compose.runtime.Immutable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Immutable
sealed class ResinStatusWidgetConfigurationDestination {
    abstract val arguments: List<NamedNavArgument>
    abstract val plainRoute: String
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

    data object EmptyScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<NamedNavArgument> = listOf()
        override val plainRoute: String = "emptyScreen"
    }

    data object LoadingScreen : ResinStatusWidgetConfigurationDestination() {
        const val APP_WIDGET_ID = "appWidgetId"

        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(APP_WIDGET_ID) {
                type = NavType.IntType
                defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
            }
        )
        override val plainRoute: String = "loadingScreen"

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    data object CreateResinStatusWidgetScreen : ResinStatusWidgetConfigurationDestination() {
        const val APP_WIDGET_ID = "appWidgetId"

        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(LoadingScreen.APP_WIDGET_ID) {
                type = NavType.IntType
                defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
            }
        )
        override val plainRoute: String = "createResinStatusWidgetScreen"

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    data object ResinStatusWidgetDetailScreen : ResinStatusWidgetConfigurationDestination() {
        const val APP_WIDGET_ID = "appWidgetId"

        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(LoadingScreen.APP_WIDGET_ID) {
                type = NavType.IntType
                defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
            }
        )
        override val plainRoute: String = "resinStatusWidgetDetailScreen"

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    data object LoginHoYoLABScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<NamedNavArgument> = listOf()
        override val plainRoute: String = "loginHoYoLABScreen"
    }
}
