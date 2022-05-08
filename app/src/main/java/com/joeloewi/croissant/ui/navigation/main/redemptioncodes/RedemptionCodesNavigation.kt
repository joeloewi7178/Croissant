package com.joeloewi.croissant.ui.navigation.main.redemptioncodes

sealed class RedemptionCodesNavigation(
    val route: String
) {
    object RedemptionCodesScreen : RedemptionCodesNavigation(
        route = "redemptionCodesScreen"
    )
}
