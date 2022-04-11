package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.startup.Initializer
import com.joeloewi.croissant.R

class NotificationChannelInitializer : Initializer<List<NotificationChannelCompat>> {

    override fun create(context: Context): List<NotificationChannelCompat> =
        context.resources.run {
            listOf(
                getString(R.string.attendance_notification_channel_id) to getString(R.string.attendance_notification_channel_name),
                getString(R.string.reminder_notification_channel_id) to getString(R.string.reminder_notification_channel_name)
            )
        }.filter { pair ->
            NotificationManagerCompat.from(context).getNotificationChannel(pair.first) == null
        }.map { pair ->
            NotificationChannelCompat
                .Builder(
                    pair.first,
                    NotificationManagerCompat.IMPORTANCE_HIGH
                )
                .setName(pair.second)
                .build()
        }.also {
            NotificationManagerCompat.from(context).createNotificationChannelsCompat(it)
        }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}