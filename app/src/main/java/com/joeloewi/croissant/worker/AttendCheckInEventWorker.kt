package com.joeloewi.croissant.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.common.NotSupportedGameException
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import dagger.assisted.Assisted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class AttendCheckInEventWorker @Inject constructor(
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
            //check session is valid
            val attendanceWithAllValues = croissantDatabase.attendanceDao().getOne(attendanceId)
            val cookie = attendanceWithAllValues.attendance.cookie
            val userFullInfo = hoYoLABService.getUserFullInfo(cookie).data!!

            //attend check in events
            attendanceWithAllValues.games.map {
                async {
                    when (it.name) {
                        HoYoLABGame.HonkaiImpact3rd -> {
                            hoYoLABService.attendCheckInHonkaiImpact3rd(cookie = cookie)
                        }
                        HoYoLABGame.GenshinImpact -> {
                            hoYoLABService.attendCheckInGenshinImpact(cookie = cookie)
                        }
                        HoYoLABGame.Unknown -> {
                            throw NotSupportedGameException()
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