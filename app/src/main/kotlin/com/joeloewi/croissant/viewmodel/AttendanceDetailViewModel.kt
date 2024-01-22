package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.domain.entity.relational.AttendanceWithGames
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.GameUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsILCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
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
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceDetailScreen.ATTENDANCE_ID
    val attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE

    private val _attendanceWithGamesState = MutableStateFlow<LCE<AttendanceWithGames>>(LCE.Loading)
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(ZonedDateTime.now().hour)
    private val _minute = MutableStateFlow(ZonedDateTime.now().minute)
    private val _nickname = MutableStateFlow("")
    private val _uid = MutableStateFlow(0L)
    private val _updateAttendanceState = MutableStateFlow<ILCE<Unit>>(ILCE.Idle)
    private val _deleteAttendanceState = MutableStateFlow<ILCE<Unit>>(ILCE.Idle)

    val checkedGames = mutableStateListOf<Game>()
    val attendanceWithGamesState = _attendanceWithGamesState.asStateFlow()
    val hourOfDay = _hourOfDay.asStateFlow()
    val minute = _minute.asStateFlow()
    val nickname = _nickname.asStateFlow()
    val uid = _uid.asStateFlow()

    //log count
    val checkSessionWorkerSuccessLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.SUCCESS
        ).catch { }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val checkSessionWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.FAILURE
        ).catch { }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerSuccessLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.SUCCESS
        ).catch { }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.FAILURE
        ).catch { }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val updateAttendanceState = _updateAttendanceState.asStateFlow()
    val deleteAttendanceState = _deleteAttendanceState.asStateFlow()

    fun setCookie(cookie: String) {
        _cookie.value = cookie
    }

    fun setHourOfDay(hourOfDay: Int) {
        _hourOfDay.value = hourOfDay
    }

    fun setMinute(minute: Int) {
        _minute.value = minute
    }

    init {
        _attendanceWithGamesState.value = LCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _attendanceWithGamesState.value = getOneAttendanceUseCase.runCatching {
                invoke(attendanceId)
            }.mapCatching { attendanceWithGames ->
                attendanceWithGames.also {
                    with(attendanceWithGames) {
                        with(attendance) {
                            _cookie.value = cookie
                            _hourOfDay.value = hourOfDay
                            _minute.value = minute
                            _nickname.value = nickname
                            _uid.value = uid
                        }

                        withContext(Dispatchers.Main) {
                            checkedGames.addAll(games.map {
                                Game(
                                    type = it.type
                                )
                            })
                        }
                    }
                }
            }.foldAsLce()
        }
    }

    fun updateAttendance() {
        _updateAttendanceState.value = ILCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _updateAttendanceState.value = getOneAttendanceUseCase.runCatching {
                invoke(attendanceId)
            }.mapCatching { attendanceWithGames ->
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
                    cookie = _cookie.value,
                    nickname = _nickname.value,
                    uid = _uid.value,
                    hourOfDay = _hourOfDay.value,
                    minute = _minute.value,
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

                if (checkedGames.isEmpty()) {
                    deleteGameUseCase(*games.toTypedArray())
                } else {
                    games.forEach { game ->
                        if (!checkedGames.contains(
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

                    checkedGames.forEach { game ->
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
                Unit
            }.foldAsILCE()
        }
    }

    fun deleteAttendance() {
        viewModelScope.launch(Dispatchers.IO) {
            Firebase.analytics.logEvent("delete_attendance", bundleOf())

            _deleteAttendanceState.value = ILCE.Loading
            _deleteAttendanceState.value = runCatching {
                val attendance = getOneAttendanceUseCase(attendanceId).attendance

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
                Unit
            }.foldAsILCE()
        }
    }
}