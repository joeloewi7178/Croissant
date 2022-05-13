package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.croissant.worker.CheckSessionWorker
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.GameUseCase
import com.joeloewi.domain.usecase.HoYoLABUseCase
import com.joeloewi.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.domain.wrapper.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AttendanceDetailViewModel @Inject constructor(
    getCountByStateWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetCountByState,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val deleteGameUseCase: GameUseCase.Delete,
    private val insertGameUseCase: GameUseCase.Insert,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceDetailScreen.ATTENDANCE_ID
    val attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE

    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])
    private val _nickname = MutableStateFlow("")
    private val _uid = MutableStateFlow(0L)
    private val _updateAttendanceState = MutableStateFlow<Lce<Unit?>>(Lce.Content(null))

    val checkedGames = mutableStateListOf<Game>()
    val cookie = _cookie.asStateFlow()
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
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val checkSessionWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.FAILURE
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerSuccessLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.SUCCESS
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerFailureLogCount =
        getCountByStateWorkerExecutionLogUseCase(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.FAILURE
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val updateAttendanceState = _updateAttendanceState.asStateFlow()

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
        viewModelScope.launch(Dispatchers.IO) {
            getOneAttendanceUseCase.runCatching {
                invoke(attendanceId)
            }.mapCatching { attendanceWithGames ->
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
            }.onSuccess {

            }.onFailure {

            }
        }

        _cookie.filter { it.isNotEmpty() }.map { cookie ->
            getUserFullInfoHoYoLABUseCase(cookie)
        }.onEach {
            it.getOrThrow().data?.userInfo?.run {
                _uid.value = uid
                _nickname.value = nickname
            }
        }.flowOn(Dispatchers.IO).catch { }.launchIn(viewModelScope)
    }

    fun updateAttendance() {
        _updateAttendanceState.value = Lce.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _updateAttendanceState.value = getOneAttendanceUseCase.runCatching {
                invoke(attendanceId)
            }.mapCatching { attendanceWithGames ->
                val attendance = attendanceWithGames.attendance
                val now = Calendar.getInstance()
                val canExecuteToday =
                    (now[Calendar.HOUR_OF_DAY] < _hourOfDay.value) || (now[Calendar.HOUR_OF_DAY] == _hourOfDay.value && now[Calendar.MINUTE] < _minute.value)

                val targetTime = Calendar.getInstance().apply {
                    time = now.time

                    if (!canExecuteToday) {
                        add(Calendar.DATE, 1)
                    }

                    set(Calendar.HOUR_OF_DAY, _hourOfDay.value)
                    set(Calendar.MINUTE, _minute.value)
                }

                val periodicAttendanceCheckInEventWork = PeriodicWorkRequest.Builder(
                    AttendCheckInEventWorker::class.java,
                    24L,
                    TimeUnit.HOURS
                )
                    .setInitialDelay(
                        targetTime.timeInMillis - now.timeInMillis,
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(workDataOf(AttendCheckInEventWorker.ATTENDANCE_ID to attendance.id))
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                WorkManager.getInstance(application)
                    .enqueueUniquePeriodicWork(
                        attendance.attendCheckInEventWorkerName.toString(),
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicAttendanceCheckInEventWork
                    )

                val periodicCheckSessionWork = PeriodicWorkRequest.Builder(
                    CheckSessionWorker::class.java,
                    6L,
                    TimeUnit.HOURS
                )
                    .setInputData(workDataOf(CheckSessionWorker.ATTENDANCE_ID to attendance.id))
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                WorkManager.getInstance(application)
                    .enqueueUniquePeriodicWork(
                        attendance.checkSessionWorkerName.toString(),
                        ExistingPeriodicWorkPolicy.REPLACE,
                        periodicCheckSessionWork
                    )

                updateAttendanceUseCase(
                    attendance.copy(
                        modifiedAt = System.currentTimeMillis(),
                        cookie = _cookie.value,
                        nickname = _nickname.value,
                        uid = _uid.value,
                        hourOfDay = _hourOfDay.value,
                        minute = _minute.value,
                        timezoneId = ZoneId.systemDefault().id,
                        attendCheckInEventWorkerId = periodicAttendanceCheckInEventWork.id,
                        checkSessionWorkerId = periodicCheckSessionWork.id
                    )
                )

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
            }.fold(
                onSuccess = {
                    Lce.Content(Unit)
                },
                onFailure = {
                    it.printStackTrace()
                    Lce.Error(it)
                }
            )
        }
    }
}