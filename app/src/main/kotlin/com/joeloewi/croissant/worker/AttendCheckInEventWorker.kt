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
import com.joeloewi.croissant.R
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
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@HiltWorker
class AttendCheckInEventWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val attendCheckInGenshinImpactUseCase: CheckInUseCase.AttendCheckInGenshinImpact,
    private val attendCheckInHonkaiImpact3rdUseCase: CheckInUseCase.AttendCheckInHonkaiImpact3rd,
    private val attendCheckInTearsOfThemisUseCase: CheckInUseCase.AttendCheckInTearsOfThemis,
    private val attendCheckInHonkaiStarRailUseCase: CheckInUseCase.AttendCheckInHonkaiStarRailUseCase,
    private val attendCheckInZenlessZoneZeroUseCase: CheckInUseCase.AttendCheckInZenlessZoneZeroUseCase,
    private val insertWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.Insert,
    private val hasExecutedAtLeastOnce: WorkerExecutionLogUseCase.HasExecutedAtLeastOnce,
    private val insertSuccessLogUseCase: SuccessLogUseCase.Insert,
    private val insertFailureLogUseCase: FailureLogUseCase.Insert,
    private val notificationGenerator: NotificationGenerator,
) : CoroutineWorker(
    appContext = context,
    params = params
) {
    private val _attendanceId by lazy { inputData.getLong(ATTENDANCE_ID, Long.MIN_VALUE) }
    private val _firstTriggeredTimestamp by lazy {
        inputData.getLong(FIRST_TRIGGERED_TIMESTAMP, Instant.now().toEpochMilli())
    }
    private val _isInstantAttendance by lazy { inputData.getBoolean(IS_INSTANT_ATTENDANCE, false) }

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
        gameName: HoYoLABGame = HoYoLABGame.Unknown,
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
                gameName = gameName,
                failureMessage = cause.message ?: "",
                failureStackTrace = cause.stackTraceToString()
            )
        )
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setForeground(notificationGenerator.createForegroundInfo(_attendanceId.toInt()))

        Firebase.crashlytics.log(this@AttendCheckInEventWorker.javaClass.simpleName)
        Firebase.analytics.logEvent("attend_check_in_event", bundleOf())

        if (ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(_firstTriggeredTimestamp),
                ZoneId.systemDefault()
            ).toLocalDate().isBefore(ZonedDateTime.now().toLocalDate())
        ) {
            //date has changed while retrying, since retry has backoff
            //do not retry
            return@withContext Result.success()
        }

        if (_attendanceId == Long.MIN_VALUE) {
            //wrong value for attendance id
            //do not retry
            return@withContext Result.success()
        }

        runCatching {
            val attendanceWithGames = getOneAttendanceUseCase(_attendanceId)
            val cookie = attendanceWithGames.attendance.cookie

            //attend check in events
            attendanceWithGames.games.filter { game ->
                if (runAttemptCount == 0) {
                    //do check-in for all registered games at first attempt
                    true
                } else {
                    //from second attempt, filter games that have to be retried
                    //games that does not have success or failure logs
                    !hasExecutedAtLeastOnce(
                        attendanceId = _attendanceId,
                        gameName = game.type,
                        timestamp = _firstTriggeredTimestamp
                    )
                }
            }.map { game ->
                runCatching {
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
                            attendCheckInHonkaiStarRailUseCase(cookie = cookie)
                        }

                        HoYoLABGame.ZenlessZoneZero -> {
                            attendCheckInZenlessZoneZeroUseCase(cookie = cookie)
                        }

                        HoYoLABGame.Unknown -> {
                            kotlin.Result.failure(UnknownHoYoLABGameException())
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
                                attendanceId = _attendanceId,
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
                }.fold(
                    onSuccess = {
                        Result.success()
                    },
                    onFailure = { cause ->
                        when (cause) {
                            is HoYoLABUnsuccessfulResponseException -> {
                                when (val retCode = HoYoLABRetCode.findByCode(cause.retCode)) {
                                    HoYoLABRetCode.TooManyRequests, HoYoLABRetCode.TooManyRequestsGenshinImpact -> {
                                        if (_isInstantAttendance) {
                                            //make log and do not retry if this work was enqueued by clicking instant attendance

                                            addFailureLog(_attendanceId, game.type, cause)

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

                                            Result.success()
                                        } else {
                                            //do not make log, not to skip this game when retry
                                            notificationGenerator.createAttendanceRetryScheduledNotification(
                                                nickname = attendanceWithGames.attendance.nickname,
                                                contentText = context.getString(R.string.attendance_retry_too_many_requests_error)
                                            ).let { notification ->
                                                notificationGenerator.safeNotify(
                                                    UUID.randomUUID().toString(),
                                                    game.type.gameId,
                                                    notification
                                                )
                                            }

                                            //good to retry
                                            Result.retry()
                                        }
                                    }

                                    else -> {
                                        addFailureLog(_attendanceId, game.type, cause)

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

                                        if (retCode !in listOf(
                                                HoYoLABRetCode.AlreadyCheckedIn,
                                                HoYoLABRetCode.CharacterNotExists,
                                                HoYoLABRetCode.LoginFailed
                                            )
                                        ) {
                                            //we don't know which error was occurred
                                            //record this error for monitoring
                                            Firebase.crashlytics.recordException(cause)
                                        }

                                        //no need to retry
                                        Result.success()
                                    }
                                }
                            }

                            is UnknownHoYoLABGameException -> {
                                //can't retry, we don't know which api is required for this game
                                Result.success()
                            }

                            is CancellationException -> {
                                throw cause
                            }

                            else -> {
                                if (_isInstantAttendance) {
                                    //make log and do not retry if this work was enqueued by clicking instant attendance
                                    addFailureLog(_attendanceId, game.type, cause)

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

                                    Result.success()
                                } else {
                                    //do not make log, not to pass this game when retry
                                    notificationGenerator.createAttendanceRetryScheduledNotification(
                                        nickname = attendanceWithGames.attendance.nickname
                                    ).let { notification ->
                                        notificationGenerator.safeNotify(
                                            UUID.randomUUID().toString(),
                                            game.type.gameId,
                                            notification
                                        )
                                    }

                                    //these errors are not hoyolab server's errors, but networks errors, generally
                                    //do retry
                                    Firebase.crashlytics.log("runAttemptCount: $runAttemptCount")
                                    Result.retry()
                                }
                            }
                        }
                    }
                )
            }
        }.fold(
            onSuccess = { results ->
                //do not retry if this work was enqueued by clicking instant attendance
                if (results.contains(Result.retry()) && !_isInstantAttendance) {
                    return@withContext Result.retry()
                }

                runAttemptCount.takeIf { count -> count > 0 }?.let {
                    Firebase.crashlytics.log("succeed after run attempts: $it")
                }

                Result.success()
            },
            onFailure = { cause ->
                if (cause is CancellationException) {
                    throw cause
                }

                //let chained works do their jobs
                Result.success()
            }
        )
    }

    class UnknownHoYoLABGameException : Exception()

    companion object {
        const val ATTENDANCE_ID = "attendanceId"
        const val FIRST_TRIGGERED_TIMESTAMP = "triggeredTimestamp"
        const val IS_INSTANT_ATTENDANCE = "isInstantAttendance"

        fun buildOneTimeWork(
            attendanceId: Long,
            triggeredTimestamp: Long = Instant.now().toEpochMilli(),
            isInstantAttendance: Boolean = false,
            constraints: Constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ) = OneTimeWorkRequestBuilder<AttendCheckInEventWorker>()
            .setInputData(
                workDataOf(
                    ATTENDANCE_ID to attendanceId,
                    FIRST_TRIGGERED_TIMESTAMP to triggeredTimestamp,
                    IS_INSTANT_ATTENDANCE to isInstantAttendance
                )
            )
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(constraints)
            .build()
    }
}