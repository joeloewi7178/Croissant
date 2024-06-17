package com.joeloewi.croissant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.core.data.model.FailureLog
import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.WorkerExecutionLog
import com.joeloewi.croissant.core.data.model.WorkerExecutionLogState
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.FailureLogUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.domain.usecase.SuccessLogUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker
class CheckSessionWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    private val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    private val insertFailureLogUseCase: FailureLogUseCase.Insert,
    private val notificationGenerator: NotificationGenerator
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _attendanceId = inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Firebase.crashlytics.log(this@CheckSessionWorker.javaClass.simpleName)

        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            val attendance = runCatching { getOneAttendanceUseCase(attendanceId) }.getOrNull()

            if (attendance == null) {
                WorkManager.getInstance(context).cancelWorkById(id)

                //let chained works do their jobs
                return@withContext Result.success()
            } else {
                attendance
            }
        }.mapCatching { attendanceWithAllValues ->
            getUserFullInfoHoYoLABUseCase(attendanceWithAllValues.attendance.cookie).getOrThrow()
        }.fold(
            onSuccess = { userFullInfo ->
                val executionLogId = insertWorkerExecutionLogUseCase(
                    WorkerExecutionLog(
                        attendanceId = _attendanceId,
                        state = WorkerExecutionLogState.SUCCESS,
                        loggableWorker = LoggableWorker.CHECK_SESSION
                    )
                )

                /*insertSuccessLogUseCase(
                    SuccessLog(
                        executionLogId = executionLogId,
                        retCode = userFullInfo,
                        message = userFullInfo.message
                    )
                )*/

                runAttemptCount.takeIf { count -> count > 0 }?.let {
                    Firebase.crashlytics.log("succeed after run attempts: $it")
                }

                Result.success()
            },
            onFailure = { cause ->
                when (cause) {
                    is com.joeloewi.croissant.core.data.model.exception.HoYoLABUnsuccessfulResponseException -> {
                        if (com.joeloewi.croissant.core.data.model.HoYoLABRetCode.findByCode(cause.retCode) == com.joeloewi.croissant.core.data.model.HoYoLABRetCode.LoginFailed) {
                            with(notificationGenerator) {
                                safeNotify(
                                    UUID.randomUUID().toString(),
                                    0,
                                    createCheckSessionNotification(_attendanceId)
                                )
                            }
                        } else {
                            Firebase.crashlytics.recordException(cause)
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

                        //let chained works do their jobs
                        Result.success()
                    }

                    is CancellationException -> {
                        throw cause
                    }

                    else -> {
                        Firebase.crashlytics.log("runAttemptCount: $runAttemptCount")
                        Result.retry()
                    }
                }
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"

        fun buildPeriodicWork(
            repeatInterval: Long = 6,
            repeatIntervalTimeUnit: TimeUnit = TimeUnit.HOURS,
            constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build(),
            attendanceId: Long
        ) = PeriodicWorkRequestBuilder<CheckSessionWorker>(
            repeatInterval,
            repeatIntervalTimeUnit
        )
            .setInputData(workDataOf(ATTENDANCE_ID to attendanceId))
            .setConstraints(constraints)
            .build()
    }
}