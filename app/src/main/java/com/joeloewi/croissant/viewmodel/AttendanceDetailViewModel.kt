package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.data.common.LoggableWorker
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Game
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.data.remote.model.response.UserFullInfoResponse
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.croissant.worker.CheckSessionWorker
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
    private val croissantDatabase: CroissantDatabase,
    private val hoYoLABService: HoYoLABService,
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
    val connectedGames =
        combine(_cookie.filter { it.isNotEmpty() }, _uid.filter { it != 0L }) { cookie, uid ->
            hoYoLABService.runCatching {
                getGameRecordCard(
                    cookie = cookie,
                    uid = uid
                ).data!!
            }.fold(
                onSuccess = { gameRecordCardData ->
                    gameRecordCardData.list.map { gameRecord ->
                        gameRecord.copy(
                            hoYoLABGame = HoYoLABGame.findByGameId(gameId = gameRecord.gameId)
                        )
                    }.let { gameRecords ->
                        Lce.Content(gameRecords)
                    }
                },
                onFailure = {
                    Lce.Error(it)
                }
            )
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Lce.Loading
        )

    //log count
    val checkSessionWorkerSuccessLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.SUCCESS
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val checkSessionWorkerFailureLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.FAILURE
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerSuccessLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            loggableWorker = LoggableWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.SUCCESS
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerFailureLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
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
            croissantDatabase.attendanceDao().runCatching {
                getOne(attendanceId)
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
                            it.copy(
                                id = 0,
                                attendanceId = 0
                            )
                        })
                    }
                }
            }.onSuccess {

            }.onFailure {

            }
        }

        _cookie.filter { it.isNotEmpty() }.map { cookie ->
            hoYoLABService.getUserFullInfo(cookie = cookie)
        }.map { userFullInfoResponse ->
            userFullInfoResponse.data?.userInfo?.run {
                _uid.value = uid
                _nickname.value = nickname
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    fun updateAttendance() {
        _updateAttendanceState.value = Lce.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _updateAttendanceState.value = croissantDatabase.attendanceDao().runCatching {
                getOne(attendanceId)
            }.mapCatching { attendanceWithGames ->
                val attendance = attendanceWithGames.attendance
                val now = Calendar.getInstance()
                val canExecuteToday =
                    (now[Calendar.HOUR_OF_DAY] < attendance.hourOfDay) || (now[Calendar.HOUR_OF_DAY] == attendance.hourOfDay && now[Calendar.MINUTE] < attendance.minute)

                val targetTime = Calendar.getInstance().apply {
                    time = now.time

                    if (!canExecuteToday) {
                        add(Calendar.DATE, 1)
                    }

                    set(Calendar.HOUR_OF_DAY, attendance.hourOfDay)
                    set(Calendar.MINUTE, attendance.minute)
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

                croissantDatabase.attendanceDao().update(
                    attendance.copy(
                        modifiedAt = System.currentTimeMillis(),
                        cookie = _cookie.value,
                        nickname = _nickname.value,
                        uid = _uid.value,
                        hourOfDay = _hourOfDay.value,
                        minute = _minute.value,
                        zoneId = ZoneId.systemDefault().id,
                        attendCheckInEventWorkerId = periodicAttendanceCheckInEventWork.id,
                        checkSessionWorkerId = periodicCheckSessionWork.id
                    )
                )

                val games = attendanceWithGames.games
                val originalGames = arrayListOf<Game>()
                val newGames = arrayListOf<Game>()

                if (checkedGames.isEmpty()) {
                    croissantDatabase.gameDao().delete(*games.toTypedArray())
                } else {
                    games.forEach { game ->
                        if (!checkedGames.contains(game.copy(id = 0, attendanceId = 0))) {
                            croissantDatabase.gameDao().delete(game)
                        } else {
                            originalGames.add(game.copy(id = 0, attendanceId = 0))
                        }
                    }

                    checkedGames.forEach { game ->
                        if (!originalGames.any { it == game }) {
                            newGames.add(
                                Game(
                                    attendanceId = attendance.id,
                                    name = game.name,
                                    region = game.region
                                )
                            )
                        }
                    }
                }

                croissantDatabase.gameDao().insert(*newGames.toTypedArray())
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