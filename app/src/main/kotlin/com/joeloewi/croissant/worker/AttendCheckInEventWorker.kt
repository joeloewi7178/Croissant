package com.joeloewi.croissant.worker

import android.content.Context
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.FailureLog
import com.joeloewi.croissant.domain.entity.SuccessLog
import com.joeloewi.croissant.domain.entity.WorkerExecutionLog
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.CheckInUseCase
import com.joeloewi.croissant.domain.usecase.FailureLogUseCase
import com.joeloewi.croissant.domain.usecase.SuccessLogUseCase
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class AttendCheckInEventWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val attendCheckInGenshinImpactUseCase: CheckInUseCase.AttendCheckInGenshinImpact,
    private val attendCheckInHonkaiImpact3rdUseCase: CheckInUseCase.AttendCheckInHonkaiImpact3rd,
    private val attendCheckInTearsOfThemisUseCase: CheckInUseCase.AttendCheckInTearsOfThemis,
    private val attendCheckInHonkaiStarRail: CheckInUseCase.AttendCheckInHonkaiStarRail,
    private val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    private val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    private val insertFailureLogUseCase: FailureLogUseCase.Insert,
    private val notificationGenerator: NotificationGenerator,
    private val canPerformDnsLookup: SystemUseCase.CanPerformDnsLookup,
    private val isNetworkVpn: SystemUseCase.IsNetworkVpn
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _attendanceId by lazy { inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE) }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        notificationGenerator.createForegroundInfo(_attendanceId.toInt())

    //with known error
    private suspend fun createUnsuccessfulAttendanceNotification(
        nickname: String,
        hoYoLABGame: HoYoLABGame,
        region: String,
        hoYoLABUnsuccessfulResponseException: HoYoLABUnsuccessfulResponseException
    ) = notificationGenerator.createSuccessfulAttendanceNotification(
        nickname = nickname,
        hoYoLABGame = hoYoLABGame,
        region = region,
        message = hoYoLABUnsuccessfulResponseException.responseMessage,
        retCode = hoYoLABUnsuccessfulResponseException.retCode
    )

    private suspend fun addFailureLog(
        attendanceId: Long,
        cause: Throwable
    ) {
        val executionLogId = insertWorkerExecutionLogUseCase(
            WorkerExecutionLog(
                attendanceId = attendanceId,
                state = WorkerExecutionLogState.FAILURE,
                loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
            )
        )

        insertFailureLogUseCase(
            FailureLog(
                executionLogId = executionLogId,
                failureMessage = cause.message ?: "",
                failureStackTrace = cause.stackTraceToString()
            )
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setForeground(notificationGenerator.createForegroundInfo(_attendanceId.toInt()))

        Firebase.crashlytics.log(this@AttendCheckInEventWorker.javaClass.simpleName)
        Firebase.analytics.logEvent("attend_check_in_event", bundleOf())

        _attendanceId.runCatching {
            takeIf { it != Long.MIN_VALUE }!!
        }.mapCatching { attendanceId ->
            //check session is valid
            val attendanceWithGames = getOneAttendanceUseCase(attendanceId)
            val cookie = attendanceWithGames.attendance.cookie

            //attend check in events
            attendanceWithGames.games.forEach { game ->
                try {
                    when (game.type) {
                        HoYoLABGame.HonkaiImpact3rd -> {
                            attendCheckInHonkaiImpact3rdUseCase(cookie = cookie)
                        }

                        HoYoLABGame.GenshinImpact -> {
                            attendCheckInGenshinImpactUseCase(cookie = cookie)
                        }

                        HoYoLABGame.TearsOfThemis -> {
                            attendCheckInTearsOfThemisUseCase(cookie = cookie)
                        }

                        HoYoLABGame.HonkaiStarRail -> {
                            attendCheckInHonkaiStarRail(cookie = cookie)
                        }

                        HoYoLABGame.Unknown -> {
                            throw Exception()
                        }
                    }.getOrThrow().also { response ->
                        notificationGenerator.createSuccessfulAttendanceNotification(
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                            region = game.region,
                            message = response.message,
                            retCode = response.retCode
                        ).let { notification ->
                            notificationGenerator.safeNotify(
                                UUID.randomUUID().toString(),
                                game.type.gameId,
                                notification
                            )
                        }

                        val executionLogId = insertWorkerExecutionLogUseCase(
                            WorkerExecutionLog(
                                attendanceId = attendanceId,
                                state = WorkerExecutionLogState.SUCCESS,
                                loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT
                            )
                        )

                        insertSuccessLogUseCase(
                            SuccessLog(
                                executionLogId = executionLogId,
                                gameName = game.type,
                                retCode = response.retCode,
                                message = response.message
                            )
                        )
                    }
                } catch (cause: CancellationException) {
                    throw cause
                } catch (cause: Throwable) {
                    if (cause is HoYoLABUnsuccessfulResponseException) {
                        when (HoYoLABRetCode.findByCode(cause.retCode)) {
                            HoYoLABRetCode.AlreadyCheckedIn, HoYoLABRetCode.CharacterNotExists -> {
                                //do not log to crashlytics
                            }

                            else -> {
                                Firebase.crashlytics.recordException(cause)
                            }
                        }

                        createUnsuccessfulAttendanceNotification(
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                            region = game.region,
                            hoYoLABUnsuccessfulResponseException = cause
                        ).let { notification ->
                            notificationGenerator.safeNotify(
                                UUID.randomUUID().toString(),
                                game.type.gameId,
                                notification
                            )
                        }
                    } else {
                        //if result is unsuccessful with unknown error
                        //retry for three times

                        /*if (runAttemptCount > 3) {
                            addFailureLog(attendanceId, cause)
                        } else {

                        }*/
                        Firebase.crashlytics.recordException(cause)

                        notificationGenerator.createUnsuccessfulAttendanceNotification(
                            nickname = attendanceWithGames.attendance.nickname,
                            hoYoLABGame = game.type,
                            attendanceId = _attendanceId
                        ).let { notification ->
                            notificationGenerator.safeNotify(
                                UUID.randomUUID().toString(),
                                game.type.gameId,
                                notification
                            )
                        }
                    }

                    addFailureLog(attendanceId, cause)
                }
            }
        }.fold(
            onSuccess = {
                Result.success()
            },
            onFailure = { cause ->
                if (cause is CancellationException) {
                    throw cause
                }

                Firebase.crashlytics.recordException(cause)

                addFailureLog(_attendanceId, cause)

                //let chained works do their jobs
                Result.success()
            }
        )
    }

    companion object {
        const val ATTENDANCE_ID = "attendanceId"

        fun buildOneTimeWork(
            attendanceId: Long,
            constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ) = OneTimeWorkRequestBuilder<AttendCheckInEventWorker>()
            .setInputData(workDataOf(ATTENDANCE_ID to attendanceId))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(constraints)
            .build()
    }
}