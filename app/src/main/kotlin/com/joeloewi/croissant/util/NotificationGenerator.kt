package com.joeloewi.croissant.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.graphics.drawable.toBitmap
import androidx.work.ForegroundInfo
import coil.imageLoader
import coil.request.ImageRequest
import com.joeloewi.croissant.R
import com.joeloewi.croissant.data.common.generateGameIntent
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationGenerator(
    private val context: Context,
) {
    private val _notificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }
    private val _attendanceNotificationChannel by lazy {
        NotificationChannelCompat.Builder(
            context.getString(R.string.attendance_notification_channel_id),
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName(context.getString(R.string.attendance_notification_channel_name))
            .build()
    }

    private val _checkSessionNotificationChannel by lazy {
        NotificationChannelCompat.Builder(
            context.getString(R.string.check_session_notification_channel_id),
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName(context.getString(R.string.check_session_notification_channel_name))
            .build()
    }

    private val _timeZoneChangedNotificationChannel by lazy {
        NotificationChannelCompat.Builder(
            context.getString(R.string.time_zone_changed_notification_channel_id),
            NotificationManagerCompat.IMPORTANCE_MAX
        )
            .setName(context.getString(R.string.time_zone_changed_notification_channel_name))
            .build()
    }

    private val _attendanceForegroundNotificationChannel by lazy {
        NotificationChannelCompat.Builder(
            context.getString(R.string.attendance_foreground_notification_channel_id),
            NotificationManagerCompat.IMPORTANCE_MIN
        )
            .setName(context.getString(R.string.attendance_foreground_notification_channel_name))
            .setVibrationPattern(null)
            .build()
    }

    private suspend fun NotificationCompat.Builder.setLargeIconViaCoil(
        resource: Any,
        context: Context
    ): NotificationCompat.Builder {
        val gameIcon = context.imageLoader.runCatching {
            execute(
                ImageRequest.Builder(context = context)
                    .data(resource)
                    .build()
            ).drawable
        }.getOrNull()

        if (gameIcon != null) {
            return setLargeIcon(gameIcon.toBitmap())
        }
        return this
    }

    fun createNotificationChannels() = _notificationManagerCompat.createNotificationChannelsCompat(
        listOf(
            _attendanceNotificationChannel,
            _checkSessionNotificationChannel,
            _timeZoneChangedNotificationChannel,
            _attendanceForegroundNotificationChannel
        )
    )

    fun createTimezoneChangedNotification(): Notification = NotificationCompat
        .Builder(context, _timeZoneChangedNotificationChannel.id)
        .setContentTitle(context.getString(R.string.time_zone_changed_notification_title))
        .setContentText(context.getString(R.string.time_zone_changed_notification_description))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    context.packageManager.getLaunchIntentForPackage(context.packageName)
                        ?: Intent(Intent.ACTION_VIEW).apply {
                            addCategory(Intent.CATEGORY_DEFAULT)
                            data = Uri.parse("market://details?id=${context.packageName}")
                        },
                    pendingIntentFlagUpdateCurrent
                )

            setContentIntent(pendingIntent)
        }
        .build()

    suspend fun createSuccessfulAttendanceNotification(
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        message: String,
        retCode: Int
    ): Notification = NotificationCompat
        .Builder(context, _attendanceNotificationChannel.id)
        .setContentTitle(
            "${
                context.getString(
                    R.string.attendance_of_nickname,
                    nickname
                )
            } - ${context.getString(hoYoLABGame.gameNameStringResId())}"
        )
        .setContentText("$message (${retCode})")
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .setLargeIconViaCoil(hoYoLABGame.gameIconUrl, context)
        .apply {
            val pendingIntentFlag = pendingIntentFlagUpdateCurrent

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    generateGameIntent(
                        context = context,
                        hoYoLABGame = hoYoLABGame,
                        region = region
                    ),
                    pendingIntentFlag
                )

            setContentIntent(pendingIntent)
        }
        .build()

    suspend fun createUnsuccessfulAttendanceNotification(
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        attendanceId: Long
    ): Notification = NotificationCompat
        .Builder(context, _attendanceNotificationChannel.id)
        .setContentTitle(
            "${
                context.getString(
                    R.string.attendance_of_nickname,
                    nickname
                )
            } - ${context.getString(hoYoLABGame.gameNameStringResId())}"
        )
        .setContentText(context.getString(R.string.attendance_failed))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .setLargeIconViaCoil(hoYoLABGame.gameIconUrl, context)
        .apply {
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.Builder()
                            .scheme(context.getString(R.string.deep_link_scheme))
                            .authority(context.packageName)
                            .appendEncodedPath(
                                AttendancesDestination.AttendanceDetailScreen()
                                    .generateRoute(attendanceId)
                            )
                            .build()
                    )
                )
                getPendingIntent(0, pendingIntentFlagUpdateCurrent)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    fun createForegroundInfo(notificationId: Int): ForegroundInfo = NotificationCompat
        .Builder(
            context,
            _attendanceForegroundNotificationChannel.id
        )
        .setContentTitle(context.getString(R.string.attendance_foreground_notification_title))
        .setContentText(context.getString(R.string.wait_for_a_moment))
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                foregroundServiceBehavior = Notification.FOREGROUND_SERVICE_IMMEDIATE
            }
        }
        .build()
        .run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return@run ForegroundInfo(
                    notificationId,
                    this,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            }
            return@run ForegroundInfo(
                notificationId,
                this
            )
        }

    fun createCheckSessionNotification(
        attendanceId: Long
    ): Notification = NotificationCompat
        .Builder(context, _checkSessionNotificationChannel.id)
        .setContentTitle(context.getString(R.string.check_session_notification_title))
        .setContentText(context.getString(R.string.check_session_notification_description))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.Builder()
                            .scheme(context.getString(R.string.deep_link_scheme))
                            .authority(context.packageName)
                            .appendEncodedPath(
                                AttendancesDestination.AttendanceDetailScreen()
                                    .generateRoute(attendanceId)
                            )
                            .build()
                    )
                )
                getPendingIntent(0, pendingIntentFlagUpdateCurrent)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    suspend fun safeNotify(
        tag: String,
        notificationId: Int,
        notification: Notification
    ) = withContext(Dispatchers.IO) {
        if (context.packageManager.checkPermission(
                CroissantPermission.PostNotifications.permission,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                tag,
                notificationId,
                notification
            )
        }
    }
}