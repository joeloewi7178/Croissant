package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.core.model.Attendance
import com.joeloewi.croissant.core.model.Game
import com.joeloewi.croissant.core.model.LoggableWorker
import com.joeloewi.croissant.core.model.WorkerExecutionLogState
import com.joeloewi.croissant.domain.AttendanceUseCase
import com.joeloewi.croissant.domain.GameUseCase
import com.joeloewi.croissant.domain.WorkerExecutionLogUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AttendanceDetailViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val alarmScheduler: AlarmScheduler,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete,
    private val deleteGameUseCase: GameUseCase.Delete,
    private val insertGameUseCase: GameUseCase.Insert,
    getCountByStateWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetCountByState,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    ContainerHost<AttendanceDetailViewModel.AttendanceDetailState, AttendanceDetailViewModel.AttendanceDetailSideEffect> {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceDetailScreen.ATTENDANCE_ID
    private val _attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    private val _checkSessionWorkerSuccessLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = _attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.SUCCESS
        ).catch { }.flowOn(Dispatchers.IO)
    private val _checkSessionWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = _attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.FAILURE
        ).catch { }.flowOn(Dispatchers.IO)
    private val _attendCheckInEventWorkerSuccessLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = _attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.SUCCESS
        ).catch { }.flowOn(Dispatchers.IO)
    private val _attendCheckInEventWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = _attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.FAILURE
        ).catch { }.flowOn(Dispatchers.IO)

    override val container =
        container<AttendanceDetailState, AttendanceDetailSideEffect>(AttendanceDetailState()) {
            intent {
                val attendanceWithGamesResult =
                    runCatching { getOneAttendanceUseCase(_attendanceId) }.onSuccess {
                        withContext(Dispatchers.Main) {
                            state.checkedGames.addAll(it.games.map { Game(type = it.type) })
                        }
                    }

                reduce {
                    state.copy(
                        attendance = attendanceWithGamesResult.mapCatching { it.attendance }
                            .foldAsLce()
                    )
                }
            }

            intent {
                _checkSessionWorkerSuccessLogCount.collect {
                    reduce { state.copy(checkSessionWorkerSuccessLogCount = it) }
                }
            }

            intent {
                _checkSessionWorkerFailureLogCount.collect {
                    reduce { state.copy(checkSessionWorkerFailureLogCount = it) }
                }
            }

            intent {
                _attendCheckInEventWorkerSuccessLogCount.collect {
                    reduce { state.copy(attendCheckInEventWorkerSuccessLogCount = it) }
                }
            }

            intent {
                _attendCheckInEventWorkerFailureLogCount.collect {
                    reduce { state.copy(attendCheckInEventWorkerFailureLogCount = it) }
                }
            }
        }

    fun setCookie(cookie: String) = intent {
        reduce {
            val attendance = state.attendance

            if (attendance is LCE.Content) {
                state.copy(attendance = LCE.Content(attendance.content.copy(cookie = cookie)))
            } else {
                state
            }
        }
    }

    fun setHourOfDay(hourOfDay: Int) = intent {
        reduce {
            val attendance = state.attendance

            if (attendance is LCE.Content) {
                state.copy(attendance = LCE.Content(attendance.content.copy(hourOfDay = hourOfDay)))
            } else {
                state
            }
        }
    }

    fun setMinute(minute: Int) = intent {
        reduce {
            val attendance = state.attendance

            if (attendance is LCE.Content) {
                state.copy(attendance = LCE.Content(attendance.content.copy(minute = minute)))
            } else {
                state
            }
        }
    }

    fun updateAttendance() {
        intent {
            val cachedAttendance = state.attendance

            if (cachedAttendance is LCE.Content) {
                postSideEffect(AttendanceDetailSideEffect.Dialog(shouldShow = true))

                val attendanceWithGames = getOneAttendanceUseCase(_attendanceId)
                val attendance = attendanceWithGames.attendance

                workManager.cancelUniqueWork(attendance.attendCheckInEventWorkerName.toString())

                val periodicCheckSessionWork = CheckSessionWorker.buildPeriodicWork(
                    attendanceId = attendance.id
                )

                workManager.enqueueUniquePeriodicWork(
                    attendance.checkSessionWorkerName.toString(),
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    periodicCheckSessionWork
                )

                val newAttendance = attendance.copy(
                    modifiedAt = Instant.now().toEpochMilli(),
                    cookie = cachedAttendance.content.cookie,
                    nickname = cachedAttendance.content.nickname,
                    uid = cachedAttendance.content.uid,
                    hourOfDay = cachedAttendance.content.hourOfDay,
                    minute = cachedAttendance.content.minute,
                    timezoneId = ZoneId.systemDefault().id,
                    checkSessionWorkerId = periodicCheckSessionWork.id
                )

                alarmScheduler.scheduleCheckInAlarm(
                    attendance = newAttendance,
                    scheduleForTomorrow = false
                )

                updateAttendanceUseCase(newAttendance)

                val games = attendanceWithGames.games
                val originalGames = arrayListOf<Game>()
                val newGames = arrayListOf<Game>()

                if (state.checkedGames.isEmpty()) {
                    deleteGameUseCase(*games.toTypedArray())
                } else {
                    games.forEach { game ->
                        if (!state.checkedGames.contains(
                                Game(
                                    type = game.type
                                )
                            )
                        ) {
                            deleteGameUseCase(game)
                        } else {
                            originalGames.add(
                                Game(
                                    type = game.type
                                )
                            )
                        }
                    }

                    state.checkedGames.forEach { game ->
                        if (!originalGames.any { it == game }) {
                            newGames.add(
                                Game(
                                    attendanceId = attendance.id,
                                    roleId = game.roleId,
                                    type = game.type,
                                    region = game.region
                                )
                            )
                        }
                    }
                }

                insertGameUseCase(*newGames.toTypedArray())
                postSideEffect(AttendanceDetailSideEffect.Dialog(shouldShow = false))
                postSideEffect(AttendanceDetailSideEffect.NavigateUp)
            }
        }
    }

    fun deleteAttendance() {
        intent {
            postSideEffect(AttendanceDetailSideEffect.Dialog(shouldShow = true))

            Firebase.analytics.logEvent("delete_attendance", bundleOf())

            val attendance = getOneAttendanceUseCase(_attendanceId).attendance

            listOf(
                attendance.checkSessionWorkerName,
                attendance.attendCheckInEventWorkerName,
                attendance.oneTimeAttendCheckInEventWorkerName
            ).map { it.toString() }.map { uniqueWorkName ->
                workManager.cancelUniqueWork(uniqueWorkName)
            }

            workManager.cancelWorkById(attendance.checkSessionWorkerId)

            alarmScheduler.cancelCheckInAlarm(attendance.id)

            deleteAttendanceUseCase(attendance)
            postSideEffect(AttendanceDetailSideEffect.Dialog(shouldShow = false))
            postSideEffect(AttendanceDetailSideEffect.NavigateUp)
        }
    }

    fun onClickLogSummary(loggableWorker: LoggableWorker) {
        intent {
            postSideEffect(AttendanceDetailSideEffect.NavigateToLog(_attendanceId, loggableWorker))
        }
    }

    fun onShowConfirmDeleteDialogChange(showConfirmDeleteDialog: Boolean) {
        intent {

        }
    }

    data class AttendanceDetailState(
        val attendance: LCE<Attendance> = LCE.Loading,
        val checkedGames: SnapshotStateList<Game> = mutableStateListOf(),
        val checkSessionWorkerSuccessLogCount: Long = 0,
        val checkSessionWorkerFailureLogCount: Long = 0,
        val attendCheckInEventWorkerSuccessLogCount: Long = 0,
        val attendCheckInEventWorkerFailureLogCount: Long = 0
    ) {
        fun hasExecutedAtLeastOnce() =
            attendCheckInEventWorkerSuccessLogCount > 0 || attendCheckInEventWorkerFailureLogCount > 0
    }

    sealed class AttendanceDetailSideEffect {
        data object NavigateUp : AttendanceDetailSideEffect()
        data class Dialog(
            val shouldShow: Boolean
        ) : AttendanceDetailSideEffect()

        data class NavigateToLog(
            val attendanceId: Long,
            val loggableWorker: LoggableWorker
        ) : AttendanceDetailSideEffect()
    }
}