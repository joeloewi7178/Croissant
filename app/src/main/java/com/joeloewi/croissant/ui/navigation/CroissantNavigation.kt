package com.joeloewi.croissant.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Event
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
    object Attendances :
        CroissantNavigation(
            route = "attendances",
            filledIcon = Icons.Filled.TaskAlt,
            outlinedIcon = Icons.Outlined.TaskAlt,
            resourceId = R.string.navigation_label_attendance
        )

    object Reminders :
        CroissantNavigation(
            route = "reminders",
            filledIcon = Icons.Filled.Event,
            outlinedIcon = Icons.Outlined.Event,
            resourceId = R.string.navigation_label_reminder
        )

    object Settings :
        CroissantNavigation(
            route = "settings",
            filledIcon = Icons.Filled.Settings,
            outlinedIcon = Icons.Outlined.Settings,
            resourceId = R.string.navigation_label_settings
        )
}
