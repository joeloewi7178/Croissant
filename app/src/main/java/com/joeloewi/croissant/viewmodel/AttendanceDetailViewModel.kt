package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.receiver.AlarmReceiver
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.CheckSessionWorker
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.GameUseCase
import com.joeloewi.domain.usecase.WorkerExecutionLogUseCase
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
    private val alarmManager: AlarmManager,
    getCountByStateWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetCountByState,
    private val getOneAttendanceUseCase: AttendanceUseCase.GetOne,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val deleteGameUseCase: GameUseCase.Delete,
    private val insertGameUseCase: GameUseCase.Insert,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceDetailScreen.ATTENDANCE_ID
    val attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE

    private val _attendanceWithGamesState = MutableStateFlow<Lce<AttendanceWithGames>>(Lce.Loading)
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])
    private val _nickname = MutableStateFlow("")
    private val _uid = MutableStateFlow(0L)
    private val _updateAttendanceState = MutableStateFlow<Lce<Unit?>>(Lce.Content(null))

    val checkedGames = mutableStateListOf<Game>()
    val attendanceWithGamesState = _attendanceWithGamesState.asStateFlow()
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
        viewModelScope.launch(Dispatchers.IO) {
            _cookie.update { cookie }
        }
    }

    fun setHourOfDay(hourOfDay: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _hourOfDay.update { hourOfDay }
        }
    }

    fun setMinute(minute: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _minute.update { minute }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _attendanceWithGamesState.update { Lce.Loading }
            _attendanceWithGamesState.update {
                getOneAttendanceUseCase.runCatching {
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
                }.fold(
                    onSuccess = {
                        Lce.Content(it)
                    },
                    onFailure = {
                        Lce.Error(it)
                    }
                )
            }
        }
    }

    fun updateAttendance() {
        viewModelScope.launch(Dispatchers.IO) {
            _updateAttendanceState.update { Lce.Loading }
            _updateAttendanceState.update {
                getOneAttendanceUseCase.runCatching {
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
                        set(Calendar.SECOND, 30)
                    }

                    WorkManager.getInstance(application)
                        .cancelUniqueWork(attendance.attendCheckInEventWorkerName.toString())

                    val alarmPendingIntent = PendingIntent.getBroadcast(
                        application,
                        attendance.id.toInt(),
                        Intent(application, AlarmReceiver::class.java).apply {
                            action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
                            putExtra(AlarmReceiver.ATTENDANCE_ID, attendance.id)
                        },
                        pendingIntentFlagUpdateCurrent
                    )

                    with(alarmManager) {
                        cancel(alarmPendingIntent)
                        AlarmManagerCompat.setExactAndAllowWhileIdle(
                            this,
                            AlarmManager.RTC_WAKEUP,
                            targetTime.timeInMillis,
                            alarmPendingIntent
                        )
                    }

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
                        Lce.Error(it)
                    }
                )
            }
        }
    }
}