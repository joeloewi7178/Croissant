package com.joeloewi.croissant.ui.navigation.redemptioncodes

sealed class RedemptionCodesNavigation(
    val route: String
) {
    object RedemptionCodesScreen : RedemptionCodesNavigation(
        route = "redemptionCodesScreen"
    )
}
