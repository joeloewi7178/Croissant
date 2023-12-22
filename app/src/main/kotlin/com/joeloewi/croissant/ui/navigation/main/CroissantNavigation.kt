package com.joeloewi.croissant.ui.navigation.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.joeloewi.croissant.R

sealed class CroissantNavigation(
    val route: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    @StringRes val resourceId: Int
) {
    data object Attendances :
        CroissantNavigation(
            route = "attendances",
            filledIcon = Icons.Filled.TaskAlt,
            outlinedIcon = Icons.Outlined.TaskAlt,
            resourceId = R.string.navigation_label_attendance
        )

    data object RedemptionCodes :
        CroissantNavigation(
            route = "redemptionCodes",
            filledIcon = Icons.Filled.Redeem,
            outlinedIcon = Icons.Outlined.Redeem,
            resourceId = R.string.navigation_label_redemption_codes
        )

    data object Settings :
        CroissantNavigation(
            route = "settings",
            filledIcon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings,
            resourceId = R.string.navigation_label_settings
        )

    data object Global :
        CroissantNavigation(
            route = "global",
            filledIcon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings,
            resourceId = R.string.navigation_label_settings
        )
}
