package com.joeloewi.croissant.worker

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.joeloewi.croissant.R
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.common.NotSupportedGameException
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.data.remote.model.response.AttendanceResponse
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

    private suspend fun createAttendanceNotification(
        context: Context,
        channelId: String,
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        attendanceResponse: AttendanceResponse
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle("${nickname}의 출석 작업 - ${context.getString(hoYoLABGame.gameNameResourceId)}")
        .setContentText("(${attendanceResponse.retcode}) ${attendanceResponse.message}")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .apply {
            ImageLoader(context).execute(
                ImageRequest.Builder(context = context)
                    .data(hoYoLABGame.gameIconUrl)
                    .build()
            ).drawable?.run {
                setLargeIcon(toBitmap())
            }
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
                async {
                    when (game.name) {
                        HoYoLABGame.HonkaiImpact3rd -> {
                            hoYoLABService.attendCheckInHonkaiImpact3rd(cookie = cookie)
                        }
                        HoYoLABGame.GenshinImpact -> {
                            hoYoLABService.attendCheckInGenshinImpact(cookie = cookie)
                        }
                        HoYoLABGame.Unknown -> {
                            throw NotSupportedGameException()
                        }
                    }.let {
                        createAttendanceNotification(
                            context = context,
                            channelId = context.getString(R.string.attendance_notification_channel_id),
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.name,
                            attendanceResponse = it
                        ).let { notification ->
                            NotificationManagerCompat.from(context).notify(
                                UUID.randomUUID().toString(),
                                game.name.gameId,
                                notification
                            )
                        }
                    }
                }
            }.awaitAll()
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = {
                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
        const val UID = "uid"
        const val NICKNAME = "nickname"
    }
}