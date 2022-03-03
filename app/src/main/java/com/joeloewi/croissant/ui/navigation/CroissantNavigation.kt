package com.joeloewi.croissant.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.joeloewi.croissant.R

sealed class CroissantNavigation(
    val route: String,
    val imageVector: ImageVector,
    @StringRes val resourceId: Int
) {
    object Attendances :
        CroissantNavigation(
            route = "attendances",
            imageVector = Icons.Default.TaskAlt,
            resourceId = R.string.navigation_label_attendance
        )

    object Reminders :
        CroissantNavigation(
            route = "reminders",
            imageVector = Icons.Default.Event,
            resourceId = R.string.navigation_label_reminder
        )

    object Settings :
        CroissantNavigation(
            route = "settings",
            imageVector = Icons.Default.Settings,
            resourceId = R.string.navigation_label_settings
        )
}
