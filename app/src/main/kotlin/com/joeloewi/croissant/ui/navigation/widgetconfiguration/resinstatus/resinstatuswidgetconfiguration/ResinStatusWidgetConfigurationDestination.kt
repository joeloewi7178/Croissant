package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration

import android.appwidget.AppWidgetManager
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavType

sealed class ResinStatusWidgetConfigurationDestination {
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

    data object EmptyScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute: String = "emptyScreen"
    }

    class LoadingScreen(
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf(
            APP_WIDGET_ID to {
                type = NavType.IntType
                defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
            }
        ),
        override val plainRoute: String = "loadingScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    class CreateResinStatusWidgetScreen(
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf(
            APP_WIDGET_ID to { type = NavType.IntType }
        ),
        override val plainRoute: String = "createResinStatusWidgetScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    class ResinStatusWidgetDetailScreen(
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf(
            APP_WIDGET_ID to { type = NavType.IntType }
        ),
        override val plainRoute: String = "resinStatusWidgetDetailScreen"
    ) : ResinStatusWidgetConfigurationDestination() {
        companion object {
            const val APP_WIDGET_ID = "appWidgetId"
        }

        fun generateRoute(appWidgetId: Int) = "${plainRoute}/${appWidgetId}"
    }

    data object LoginHoYoLABScreen : ResinStatusWidgetConfigurationDestination() {
        override val arguments: List<Pair<String, NavArgumentBuilder.() -> Unit>> = listOf()
        override val plainRoute: String = "loginHoYoLABScreen"
    }
}
