package com.joeloewi.croissant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import dagger.assisted.Assisted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class CheckSessionWorker @Inject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    private val croissantDatabase: CroissantDatabase,
    private val hoYoLABService: HoYoLABService
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE).runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            croissantDatabase.attendanceDao().getOne(attendanceId)
        }.mapCatching { attendanceWithAllValues ->
            hoYoLABService.getUserFullInfo(
                cookie = attendanceWithAllValues.attendance.cookie
            ).data!!.userInfo
        }.fold(
            onSuccess = { userInfo ->
                Result.success(
                    workDataOf(
                        AttendCheckInEventWorker.NICKNAME to userInfo.nickname,
                        AttendCheckInEventWorker.UID to userInfo.uid
                    )
                )
            },
            onFailure = {
                Result.failure()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
    }
}