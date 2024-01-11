package com.joeloewi.croissant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.FailureLog
import com.joeloewi.croissant.domain.entity.SuccessLog
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
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
        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            val attendance = runCatching { getOneAttendanceUseCase(attendanceId) }.getOrNull()

            if (attendance == null) {
                WorkManager.getInstance(context).cancelWorkById(id)
                return@withContext Result.failure()
            } else {
                attendance
            }
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
                            with(notificationGenerator) {
                                safeNotify(
                                    UUID.randomUUID().toString(),
                                    0,
                                    createCheckSessionNotification(_attendanceId)
                                )
                            }
                        }
                    }

                    is CancellationException -> {
                        throw cause
                    }
                }

                Firebase.crashlytics.apply {
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