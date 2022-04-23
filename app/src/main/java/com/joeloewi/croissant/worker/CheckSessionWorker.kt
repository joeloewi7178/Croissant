package com.joeloewi.croissant.worker

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.joeloewi.croissant.R
import com.joeloewi.croissant.data.common.LoggableWorker
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.FailureLog
import com.joeloewi.croissant.data.local.model.SuccessLog
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class CheckSessionWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val croissantDatabase: CroissantDatabase,
    private val hoYoLABService: HoYoLABService
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val attendanceId = inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE)

    private val attendanceDetailDeepLinkUri = Uri.Builder()
        .scheme(context.getString(R.string.deep_link_scheme))
        .authority(context.packageName)
        .appendPath(AttendancesDestination.AttendanceDetailScreen().plainRoute)
        .appendPath(attendanceId.toString())
        .build()

    private fun getAttendanceDetailIntent(): Intent = Intent(
        Intent.ACTION_VIEW,
        attendanceDetailDeepLinkUri
    )

    private fun createCheckSessionNotification(
        context: Context,
        channelId: String,
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle("접속 정보 유효성 검사 실패")
        .setContentText("상세 화면에서 접속 정보를 갱신해주세요.")
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .apply {

            val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(getAttendanceDetailIntent())
                getPendingIntent(0, pendingIntentFlag)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            croissantDatabase.attendanceDao().getOne(attendanceId)
        }.mapCatching { attendanceWithAllValues ->
            hoYoLABService.getUserFullInfo(
                cookie = attendanceWithAllValues.attendance.cookie
            )
        }.mapCatching { userFullInfoData ->
            if (userFullInfoData.data == null) {
                throw NullPointerException()
            }

            userFullInfoData
        }.fold(
            onSuccess = {
                val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.SUCCESS,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                croissantDatabase.successLogDao().insert(
                    SuccessLog(
                        executionLogId = executionLogId,
                        retCode = it.retcode,
                        message = it.message
                    )
                )

                Result.success()
            },
            onFailure = { cause ->
                val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.FAILURE,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                croissantDatabase.failureLogDao().insert(
                    FailureLog(
                        executionLogId = executionLogId,
                        failureMessage = cause.message ?: "",
                        failureStackTrace = cause.stackTraceToString()
                    )
                )

                createCheckSessionNotification(
                    context = context,
                    channelId = context.getString(R.string.check_session_notification_channel_id),
                ).let { notification ->
                    NotificationManagerCompat.from(context).notify(
                        UUID.randomUUID().toString(),
                        0,
                        notification
                    )
                }

                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}