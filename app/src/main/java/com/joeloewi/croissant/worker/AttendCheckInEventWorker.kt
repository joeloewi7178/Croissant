package com.joeloewi.croissant.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.data.common.*
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.FailureLog
import com.joeloewi.croissant.data.local.model.SuccessLog
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.data.remote.model.response.AttendanceResponse
import com.joeloewi.croissant.util.CroissantPermission
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
    private val croissantDatabase: CroissantDatabase,
    private val hoYoLABService: HoYoLABService
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val attendanceId = inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE)

    private fun getIntentFromGameRegion(
        hoYoLABGame: HoYoLABGame,
        region: String
    ): Intent = when (hoYoLABGame) {
        HoYoLABGame.HonkaiImpact3rd -> {
            with(HonkaiImpact3rdServer.findByRegion(region = region)) {
                packageName to fallbackUri
            }
        }
        HoYoLABGame.GenshinImpact -> {
            with(GenshinImpactServer.findByRegion(region = region)) {
                packageName to fallbackUri
            }
        }
        HoYoLABGame.TearsOfThemis -> {
            "com.miHoYo.tot.glb" to "market://details?id=com.miHoYo.tot.glb".toUri()
        }
        HoYoLABGame.Unknown -> {
            "" to Uri.EMPTY
        }
    }.let {
        context.packageManager.getLaunchIntentForPackage(it.first)
            ?: if (it.second.authority?.contains("taptap.io") == true) {
                //for chinese server
                Intent(Intent.ACTION_VIEW, it.second)
            } else {
                Intent(Intent.ACTION_VIEW).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = it.second
                }
            }
    }

    private suspend fun createAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        attendanceResponse: AttendanceResponse
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle("${nickname}의 출석 작업 - ${context.getString(hoYoLABGame.gameNameResourceId)}")
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
                    getIntentFromGameRegion(
                        hoYoLABGame = hoYoLABGame,
                        region = region
                    ),
                    pendingIntentFlag
                )

            setContentIntent(pendingIntent)
        }
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            //check session is valid
            val attendanceWithGames = croissantDatabase.attendanceDao().getOne(attendanceId)
            val cookie = attendanceWithGames.attendance.cookie

            if (hoYoLABService.getUserFullInfo(cookie).data == null) {
                throw NullPointerException()
            }

            //attend check in events
            attendanceWithGames.games.map { game ->
                //do parallel jobs
                async {
                    when (game.type) {
                        HoYoLABGame.HonkaiImpact3rd -> {
                            hoYoLABService.attendCheckInHonkaiImpact3rd(cookie = cookie)
                        }
                        HoYoLABGame.GenshinImpact -> {
                            hoYoLABService.attendCheckInGenshinImpact(cookie = cookie)
                        }
                        HoYoLABGame.TearsOfThemis -> {
                            hoYoLABService.attendTearsOfThemis(cookie = cookie)
                        }
                        HoYoLABGame.Unknown -> {
                            throw NotSupportedGameException()
                        }
                    }.also { response ->
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
                        val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                            WorkerExecutionLog(
                                attendanceId = attendanceId,
                                state = WorkerExecutionLogState.SUCCESS,
                                loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
                            )
                        )

                        croissantDatabase.successLogDao().insert(
                            SuccessLog(
                                executionLogId = executionLogId,
                                gameName = game.type,
                                retCode = response.retcode,
                                message = response.message
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
                val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.FAILURE,
                        loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
                    )
                )

                croissantDatabase.failureLogDao().insert(
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