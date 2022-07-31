package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus

sealed class ResinStatusWidgetConfigurationNavigation(
    val route: String = ""
) {
    object Configuration : ResinStatusWidgetConfigurationNavigation(
        route = "configuration"
    )
}
