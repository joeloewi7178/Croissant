package com.joeloewi.croissant.util

import android.Manifest
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R

sealed class CroissantPermission(
    val permission: String,
    val icon: ImageVector,
    @StringRes val label: Int,
    @StringRes val detailedDescription: Int,
) {
    object AccessHoYoLABSession : CroissantPermission(
        permission = "${BuildConfig.APPLICATION_ID}.permission.ACCESS_HOYOLAB_SESSION",
        icon = Icons.Default.Cookie,
        label = R.string.permission_access_hoyolab_session_label,
        detailedDescription = R.string.permission_access_hoyolab_session_detailed_description
    )

    object PostNotifications : CroissantPermission(
        permission = "${BuildConfig.APPLICATION_ID}.permission.POST_NOTIFICATIONS",
        icon = Icons.Default.Notifications,
        label = R.string.permission_post_notification_label,
        detailedDescription = R.string.permission_post_notification_detailed_description
    )

    companion object {
        fun values() = listOf(
            AccessHoYoLABSession,
            PostNotifications
        )

        val POST_NOTIFICATION_PERMISSION_COMPAT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                PostNotifications.permission
            }
    }
}
