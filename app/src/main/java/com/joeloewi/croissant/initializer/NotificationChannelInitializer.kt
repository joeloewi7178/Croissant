package com.joeloewi.croissant.initializer

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.startup.Initializer
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.CroissantPermission

class NotificationChannelInitializer : Initializer<List<NotificationChannelCompat>> {

    override fun create(context: Context): List<NotificationChannelCompat> =
        if (context.packageManager.checkPermission(
                CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.resources.run {
                listOf(
                    getString(R.string.attendance_notification_channel_id) to getString(R.string.attendance_notification_channel_name),
                    getString(R.string.check_session_notification_channel_id) to getString(R.string.check_session_notification_channel_name),
                    getString(R.string.time_zone_changed_notification_channel_id) to getString(R.string.time_zone_changed_notification_channel_name),
                    getString(R.string.attendance_foreground_notification_channel_id) to getString(R.string.attendance_foreground_notification_channel_name)
                )
            }.filter { pair ->
                NotificationManagerCompat.from(context).getNotificationChannel(pair.first) == null
            }.map { pair ->
                NotificationChannelCompat
                    .Builder(
                        pair.first,
                        NotificationManagerCompat.IMPORTANCE_MAX
                    )
                    .setName(pair.second)
                    .build()
            }.also {
                NotificationManagerCompat.from(context).createNotificationChannelsCompat(it)
            }
        } else {
            listOf()
        }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}