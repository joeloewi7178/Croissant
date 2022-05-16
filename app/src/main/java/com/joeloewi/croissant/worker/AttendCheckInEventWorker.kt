package com.joeloewi.croissant.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.data.common.generateGameIntent
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.usecase.*
import com.joeloewi.domain.wrapper.getOrThrow
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class AttendCheckInEventWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    val attendCheckInGenshinImpactHoYoLABUseCase: GenshinImpactCheckInUseCase.AttendCheckInGenshinImpact,
    val attendCheckInHonkaiImpact3rdHoYoLABUseCase: HonkaiImpact3rdCheckInUseCase.AttendCheckInHonkaiImpact3rd,
    val attendCheckInTearsOfThemisHoYoLABUseCase: TearsOfThemisCheckInUseCase.AttendCheckInTearsOfThemis,
    val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    val insertFailureLogUseCase: FailureLogUseCase.Insert
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _attendanceId = inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE)

    private suspend fun createAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        attendanceResponse: BaseResponse
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle("${context.getString(R.string.attendance_of_nickname, nickname)} - ${context.getString(hoYoLABGame.gameNameStringResId())}")
        .setContentText("${attendanceResponse.message} (${attendanceResponse.retcode})")
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            ImageLoader(context).execute(
                ImageRequest.Builder(context = context)
                    .data(hoYoLABGame.gameIconUrl)
                    .build()
            ).drawable?.run {
                setLargeIcon(toBitmap())
            }
        }
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

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            //check session is valid
            val attendanceWithGames = getOneAttendanceUseCase(attendanceId)
            val cookie = attendanceWithGames.attendance.cookie

            getUserFullInfoHoYoLABUseCase(cookie).getOrThrow()

            //attend check in events
            attendanceWithGames.games.map { game ->
                //do parallel jobs
                async {
                    try {
                        when (game.type) {
                            HoYoLABGame.HonkaiImpact3rd -> {
                                attendCheckInHonkaiImpact3rdHoYoLABUseCase(cookie)
                            }
                            HoYoLABGame.GenshinImpact -> {
                                attendCheckInGenshinImpactHoYoLABUseCase(cookie)
                            }
                            HoYoLABGame.TearsOfThemis -> {
                                attendCheckInTearsOfThemisHoYoLABUseCase(cookie)
                            }
                            HoYoLABGame.Unknown -> {
                                throw Exception()
                            }
                        }.getOrThrow().also { response ->
                            createAttendanceNotification(
                                context = context,
                                channelId = context.getString(R.string.attendance_notification_channel_id),
                                nickname = attendanceWithGames.attendance.nickname,
                                hoYoLABGame = game.type,
                                region = game.region,
                                attendanceResponse = response
                            ).let { notification ->
                                if (context.packageManager.checkPermission(
                                        CroissantPermission.PostNotifications.permission,
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
                        }.also { response ->
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
                                    retCode = response.retcode,
                                    message = response.message
                                )
                            )
                        }
                    } catch (cause: Throwable) {
                        FirebaseCrashlytics.getInstance().apply {
                            log(this@AttendCheckInEventWorker.javaClass.simpleName)
                            recordException(cause)
                        }

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
                }
            }.awaitAll()
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { cause ->
                FirebaseCrashlytics.getInstance().apply {
                    log(this@AttendCheckInEventWorker.javaClass.simpleName)
                    recordException(cause)
                }

                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = _attendanceId,
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

                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}