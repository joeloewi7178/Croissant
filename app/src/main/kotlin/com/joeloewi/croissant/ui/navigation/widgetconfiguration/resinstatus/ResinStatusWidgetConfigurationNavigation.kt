package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus

sealed class ResinStatusWidgetConfigurationNavigation(
    val route: String = ""
) {
    data object Configuration : ResinStatusWidgetConfigurationNavigation(
        route = "configuration"
    )
}
