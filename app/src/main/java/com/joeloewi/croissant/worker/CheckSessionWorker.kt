package com.joeloewi.croissant.worker

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.FailureLog
import com.joeloewi.domain.entity.SuccessLog
import com.joeloewi.domain.entity.WorkerExecutionLog
import com.joeloewi.domain.usecase.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class CheckSessionWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    val insertFailureLogUseCase: FailureLogUseCase.Insert
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
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(getAttendanceDetailIntent())
                getPendingIntent(0, pendingIntentFlagUpdateCurrent)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            getOneAttendanceUseCase(attendanceId)
        }.mapCatching { attendanceWithAllValues ->
            getUserFullInfoHoYoLABUseCase(attendanceWithAllValues.attendance.cookie)
        }.mapCatching { userFullInfoData ->
            if (userFullInfoData.data == null) {
                throw NullPointerException()
            }

            userFullInfoData
        }.fold(
            onSuccess = {
                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.SUCCESS,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                insertSuccessLogUseCase(
                    SuccessLog(
                        executionLogId = executionLogId,
                        retCode = it.retcode,
                        message = it.message
                    )
                )

                Result.success()
            },
            onFailure = { cause ->
                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.FAILURE,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                insertFailureLogUseCase(
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
                    if (context.packageManager.checkPermission(
                            CroissantPermission.PostNotifications.permission,
                            context.packageName
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        NotificationManagerCompat.from(context).notify(
                            UUID.randomUUID().toString(),
                            0,
                            notification
                        )
                    }
                }

                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}