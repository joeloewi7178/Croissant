package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.data.common.CroissantWorker
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Game
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AttendanceDetailViewModel @Inject constructor(
    private val croissantDatabase: CroissantDatabase,
    private val hoYoLABService: HoYoLABService,
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

    val checkedGames = mutableStateListOf<HoYoLABGame>()
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
                            hoYoLABGame = HoYoLABGame.values()
                                .find { hoYoLABGame -> hoYoLABGame.gameId == gameRecord.gameId }
                                ?: HoYoLABGame.Unknown
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
            started = SharingStarted.WhileSubscribed(),
            initialValue = Lce.Loading
        )

    //log count
    val checkSessionWorkerSuccessLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            croissantWorker = CroissantWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.SUCCESS
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val checkSessionWorkerFailureLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            croissantWorker = CroissantWorker.CHECK_SESSION,
            state = WorkerExecutionLogState.FAILURE
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerSuccessLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            croissantWorker = CroissantWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.SUCCESS
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )
    val attendCheckInEventWorkerFailureLogCount =
        croissantDatabase.workerExecutionLogDao().getCountByState(
            attendanceId = attendanceId,
            croissantWorker = CroissantWorker.ATTEND_CHECK_IN_EVENT,
            state = WorkerExecutionLogState.FAILURE
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0L
        )

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
            with(croissantDatabase.attendanceDao().getOne(attendanceId)) {
                with(attendance) {
                    _cookie.value = cookie
                    _hourOfDay.value = hourOfDay
                    _minute.value = minute
                    _nickname.value = nickname
                    _uid.value = uid
                }

                with(games) {
                    withContext(Dispatchers.Main) {
                        checkedGames.addAll(map { it.name })
                    }
                }
            }
        }
    }

    fun updateAttendanceWithGames() {
        viewModelScope.launch {
            val attendanceWithGames = croissantDatabase.attendanceDao().getOne(attendanceId)

            val attendance = attendanceWithGames.attendance.copy(
                cookie = _cookie.value,
                hourOfDay = _hourOfDay.value,
                minute = _minute.value
            )

            val games = attendanceWithGames.games
            val originalGames = arrayListOf<Game>()
            val newGames = arrayListOf<Game>()

            if (checkedGames.isEmpty()) {
                croissantDatabase.gameDao().delete(*games.toTypedArray())
            } else {
                games.forEach { game ->
                    if (!checkedGames.contains(game.name)) {
                        croissantDatabase.gameDao().delete(game)
                    } else {
                        originalGames.add(game)
                    }
                }

                checkedGames.forEach { hoYoLABGame ->
                    if (originalGames.any { it.name == hoYoLABGame }) {
                        newGames.add(
                            Game(
                                attendanceId = attendance.id,
                                name = hoYoLABGame,
                            )
                        )
                    }
                }
            }

            croissantDatabase.gameDao().insert(*newGames.toTypedArray())
        }
    }
}