package com.joeloewi.croissant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.joeloewi.croissant.data.common.CroissantWorker
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.FailureLog
import com.joeloewi.croissant.data.local.model.SuccessLog
import com.joeloewi.croissant.data.local.model.WorkerExecutionLog
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

            val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                WorkerExecutionLog(
                    attendanceId = attendanceId,
                    state = WorkerExecutionLogState.SUCCESS,
                    worker = CroissantWorker.CHECK_SESSION
                )
            )

            croissantDatabase.successLogDao().insert(
                SuccessLog(
                    executionLogId = executionLogId,
                    retCode = userFullInfoData.retcode,
                    message = userFullInfoData.message
                )
            )
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { cause ->
                val executionLogId = croissantDatabase.workerExecutionLogDao().insert(
                    WorkerExecutionLog(
                        attendanceId = attendanceId,
                        state = WorkerExecutionLogState.FAILURE,
                        worker = CroissantWorker.CHECK_SESSION
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