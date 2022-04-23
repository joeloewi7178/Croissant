package com.joeloewi.croissant.receiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.joeloewi.croissant.R
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
        .setContentTitle("시간대 변경 감지")
        .setContentText("이전에 작성된 작업들이 의도대로 동작하지 않을 수 있습니다.")
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .apply {
            val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    getIntentFromPackageName(
                        context = context
                    ),
                    pendingIntentFlag
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
                        NotificationManagerCompat.from(context).notify(
                            UUID.randomUUID().toString(),
                            0,
                            notification
                        )
                    }
                }
            }

            else -> {

            }
        }
    }
}