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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.FailureLog
import com.joeloewi.croissant.domain.entity.SuccessLog
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.usecase.*
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

@HiltWorker
class CheckSessionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
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

    private fun createCheckSessionNotification(
        context: Context,
        channelId: String,
    ): Notification = NotificationCompat
        .Builder(context, channelId)
        .setContentTitle(context.getString(R.string.check_session_notification_title))
        .setContentText(context.getString(R.string.check_session_notification_description))
        .setAutoCancel(true)
        .setSmallIcon(R.drawable.ic_baseline_bakery_dining_24)
        .apply {
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(getAttendanceDetailIntent(_attendanceId))
                getPendingIntent(0, pendingIntentFlagUpdateCurrent)
            }

            setContentIntent(pendingIntent)
        }
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            getOneAttendanceUseCase(attendanceId)
        }.mapCatching { attendanceWithAllValues ->
            getUserFullInfoHoYoLABUseCase(attendanceWithAllValues.attendance.cookie).getOrThrow()
        }.fold(
            onSuccess = {
                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = _attendanceId,
                        state = WorkerExecutionLogState.SUCCESS,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                insertSuccessLogUseCase(
                    SuccessLog(
                        executionLogId = executionLogId,
                        retCode = it.retCode,
                        message = it.message
                    )
                )

                Result.success()
            },
            onFailure = { cause ->
                when (cause) {
                    is HoYoLABUnsuccessfulResponseException -> {
                        if (HoYoLABRetCode.findByCode(cause.retCode) == HoYoLABRetCode.LoginFailed) {
                            createCheckSessionNotification(
                                context = context,
                                channelId = context.getString(R.string.check_session_notification_channel_id),
                            ).let { notification ->
                                if (context.packageManager.checkPermission(
                                        CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
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
                        }
                    }

                    is CancellationException -> {
                        throw cause
                    }
                }

                FirebaseCrashlytics.getInstance().apply {
                    log(this@CheckSessionWorker.javaClass.simpleName)
                    recordException(cause)
                }

                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = _attendanceId,
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

                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}