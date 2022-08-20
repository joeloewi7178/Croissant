package com.joeloewi.croissant.receiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class TimeZoneChangedReceiver @Inject constructor(
) : BroadcastReceiver() {

    private fun getIntentFromPackageName(
        context: Context
    ): Intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        ?: Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("market://details?id=${context.packageName}")
        }

    private fun createTimezoneChangedNotification(
        context: Context,
        channelId: String,
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle(context.getString(R.string.time_zone_changed_notification_title))
        .setContentText(context.getString(R.string.time_zone_changed_notification_description))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    getIntentFromPackageName(
                        context = context
                    ),
                    pendingIntentFlagUpdateCurrent
                )

            setContentIntent(pendingIntent)
        }
        .build()

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_TIMEZONE_CHANGED -> {
                context?.run {
                    createTimezoneChangedNotification(
                        context = this,
                        channelId = getString(R.string.time_zone_changed_notification_channel_id)
                    ).let { notification ->
                        if (packageManager?.checkPermission(
                                CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                                packageName
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            NotificationManagerCompat.from(this).notify(
                                UUID.randomUUID().toString(),
                                0,
                                notification
                            )
                        }
                    }
                }
            }

            else -> {

            }
        }
    }
}