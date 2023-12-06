package com.joeloewi.croissant.worker

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
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.data.common.generateGameIntent
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.FailureLog
import com.joeloewi.croissant.domain.entity.SuccessLog
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.CheckInUseCase
import com.joeloewi.croissant.domain.usecase.FailureLogUseCase
import com.joeloewi.croissant.domain.usecase.SuccessLogUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class AttendCheckInEventWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val attendCheckInGenshinImpactUseCase: CheckInUseCase.AttendCheckInGenshinImpact,
    private val attendCheckInHonkaiImpact3rdUseCase: CheckInUseCase.AttendCheckInHonkaiImpact3rd,
    private val attendCheckInTearsOfThemisUseCase: CheckInUseCase.AttendCheckInTearsOfThemis,
    private val attendCheckInHonkaiStarRail: CheckInUseCase.AttendCheckInHonkaiStarRail,
    private val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    private val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    private val insertFailureLogUseCase: FailureLogUseCase.Insert
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _attendanceId = inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE)

    private fun generateAttendanceDetailDeepLinkUri(attendanceId: Long) =
        Uri.Builder()
            .scheme(context.getString(R.string.deep_link_scheme))
            .authority(context.packageName)
            .appendEncodedPath(
                AttendancesDestination.AttendanceDetailScreen().generateRoute(attendanceId)
            )
            .build()

    private fun getAttendanceDetailIntent(attendanceId: Long): Intent = Intent(
        Intent.ACTION_VIEW,
        generateAttendanceDetailDeepLinkUri(attendanceId)
    )

    override suspend fun getForegroundInfo(): ForegroundInfo =
        createForegroundInfo(_attendanceId.toInt())

    private fun createForegroundInfo(notificationId: Int): ForegroundInfo = NotificationCompat
        .Builder(
            context,
            getOrCreateNotificationChannel(
                context.getString(R.string.attendance_foreground_notification_channel_id),
                context.getString(R.string.attendance_foreground_notification_channel_name)
            )
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
                ForegroundInfo(
                    notificationId,
                    this,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                ForegroundInfo(
                    notificationId,
                    this
                )
            }
        }

    private fun getOrCreateNotificationChannel(
        channelId: String,
        channelName: String
    ): String =
        if (NotificationManagerCompat.from(context).getNotificationChannel(channelId) != null) {
            channelId
        } else {
            channelId.also {
                val notificationChannelCompat = NotificationChannelCompat
                    .Builder(
                        it,
                        NotificationManagerCompat.IMPORTANCE_MAX
                    )
                    .setName(channelName)
                    .build()

                NotificationManagerCompat.from(context)
                    .createNotificationChannel(notificationChannelCompat)
            }
        }

    private suspend fun createSuccessfulAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        message: String,
        retCode: Int
    ): Notification = NotificationCompat
        .Builder(context, channelId)
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
        .apply {
            context.imageLoader.runCatching {
                execute(
                    ImageRequest.Builder(context = context)
                        .data(hoYoLABGame.gameIconUrl)
                        .build()
                ).drawable
            }.getOrNull()?.run {
                setLargeIcon(toBitmap())
            }
        }
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

    //with known error
    private suspend fun createUnsuccessfulAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        hoYoLABUnsuccessfulResponseException: HoYoLABUnsuccessfulResponseException
    ) = createSuccessfulAttendanceNotification(
        context = context,
        channelId = channelId,
        nickname = nickname,
        hoYoLABGame = hoYoLABGame,
        region = region,
        message = hoYoLABUnsuccessfulResponseException.responseMessage,
        retCode = hoYoLABUnsuccessfulResponseException.retCode
    )

    //with unknown error
    private suspend fun createUnsuccessfulAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
    ): Notification = NotificationCompat
        .Builder(context, channelId)
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
        .apply {
            context.imageLoader.runCatching {
                execute(
                    ImageRequest.Builder(context = context)
                        .data(hoYoLABGame.gameIconUrl)
                        .build()
                ).drawable
            }.getOrNull()?.run {
                setLargeIcon(toBitmap())
            }
        }
        .apply {
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(getAttendanceDetailIntent(_attendanceId))
                getPendingIntent(0, pendingIntentFlagUpdateCurrent)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    private suspend fun addFailureLog(
        attendanceId: Long,
        cause: Throwable
    ) {
        val executionLogId = insertWorkerExecutionLogUseCase(
            WorkerExecutionLog(
                attendanceId = attendanceId,
                state = WorkerExecutionLogState.FAILURE,
                loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
            )
        )

        insertFailureLogUseCase(
            FailureLog(
                executionLogId = executionLogId,
                failureMessage = cause.message ?: "",
                failureStackTrace = cause.stackTraceToString()
            )
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setForeground(createForegroundInfo(_attendanceId.toInt()))
        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            //check session is valid
            val attendanceWithGames = getOneAttendanceUseCase(attendanceId)
            val cookie = attendanceWithGames.attendance.cookie

            //attend check in events
            attendanceWithGames.games.forEach { game ->
                try {
                    when (game.type) {
                        HoYoLABGame.HonkaiImpact3rd -> {
                            attendCheckInHonkaiImpact3rdUseCase(cookie = cookie)
                        }

                        HoYoLABGame.GenshinImpact -> {
                            attendCheckInGenshinImpactUseCase(cookie = cookie)
                        }

                        HoYoLABGame.TearsOfThemis -> {
                            attendCheckInTearsOfThemisUseCase(cookie = cookie)
                        }

                        HoYoLABGame.HonkaiStarRail -> {
                            attendCheckInHonkaiStarRail(cookie = cookie)
                        }

                        HoYoLABGame.Unknown -> {
                            throw Exception()
                        }
                    }.getOrThrow().also { response ->
                        createSuccessfulAttendanceNotification(
                            context = context,
                            channelId = context.getString(R.string.attendance_notification_channel_id),
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                            region = game.region,
                            message = response.message,
                            retCode = response.retCode
                        ).let { notification ->
                            if (context.packageManager.checkPermission(
                                    CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                                    context.packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                NotificationManagerCompat.from(context).notify(
                                    UUID.randomUUID().toString(),
                                    game.type.gameId,
                                    notification
                                )
                            }
                        }

                        val executionLogId = insertWorkerExecutionLogUseCase(
                            WorkerExecutionLog(
                                attendanceId = attendanceId,
                                state = WorkerExecutionLogState.SUCCESS,
                                loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
                            )
                        )

                        insertSuccessLogUseCase(
                            SuccessLog(
                                executionLogId = executionLogId,
                                gameName = game.type,
                                retCode = response.retCode,
                                message = response.message
                            )
                        )
                    }
                } catch (cause: CancellationException) {
                    throw cause
                } catch (cause: Throwable) {
                    if (cause is HoYoLABUnsuccessfulResponseException) {
                        when (HoYoLABRetCode.findByCode(cause.retCode)) {
                            HoYoLABRetCode.AlreadyCheckedIn -> {
                                //do not log to crashlytics
                            }

                            else -> {
                                Firebase.crashlytics.apply {
                                    log(this@AttendCheckInEventWorker.javaClass.simpleName)
                                    recordException(cause)
                                }
                            }
                        }

                        createUnsuccessfulAttendanceNotification(
                            context = context,
                            channelId = context.getString(R.string.attendance_notification_channel_id),
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                            region = game.region,
                            hoYoLABUnsuccessfulResponseException = cause
                        ).let { notification ->
                            if (context.packageManager.checkPermission(
                                    CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                                    context.packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                NotificationManagerCompat.from(context).notify(
                                    UUID.randomUUID().toString(),
                                    game.type.gameId,
                                    notification
                                )
                            }
                        }
                    } else {
                        //if result is unsuccessful with unknown error
                        //retry for three times

                        /*if (runAttemptCount > 3) {
                            addFailureLog(attendanceId, cause)
                        } else {

                        }*/
                        FirebaseCrashlytics.getInstance().apply {
                            log(this@AttendCheckInEventWorker.javaClass.simpleName)
                            recordException(cause)
                        }

                        createUnsuccessfulAttendanceNotification(
                            context = context,
                            channelId = context.getString(R.string.attendance_notification_channel_id),
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                        ).let { notification ->
                            if (context.packageManager.checkPermission(
                                    CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                                    context.packageName
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                NotificationManagerCompat.from(context).notify(
                                    UUID.randomUUID().toString(),
                                    game.type.gameId,
                                    notification
                                )
                            }
                        }
                    }

                    addFailureLog(attendanceId, cause)
                }
            }
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { cause ->
                if (cause is CancellationException) {
                    throw cause
                }

                FirebaseCrashlytics.getInstance().apply {
                    log(this@AttendCheckInEventWorker.javaClass.simpleName)
                    recordException(cause)
                }

                addFailureLog(_attendanceId, cause)

                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}