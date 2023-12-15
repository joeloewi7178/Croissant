package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.GameUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val workManager: WorkManager,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val insertAttendanceUseCase: AttendanceUseCase.Insert,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val insertGameUseCase: GameUseCase.Insert,
    private val getOneByUidAttendanceUseCase: AttendanceUseCase.GetOneByUid
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(ZonedDateTime.now().hour)
    private val _minute = MutableStateFlow(ZonedDateTime.now().minute)
    private val _insertAttendanceState = MutableStateFlow<Lce<List<Long>>>(Lce.Content(listOf()))
    private val _userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            getUserFullInfoHoYoLABUseCase(cookie = cookie).getOrThrow().data?.userInfo
        }.flowOn(Dispatchers.IO).catch {

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val duplicatedAttendance = _userInfo
        .filterNotNull()
        .map {
            getOneByUidAttendanceUseCase(it.uid)
        }.flowOn(Dispatchers.IO).catch {

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val connectedGames = _userInfo
        .filterNotNull()
        .combine(_cookie) { userInfo, cookie ->
            userInfo to cookie
        }.map { pair ->
            checkedGames.clear()
            getGameRecordCardHoYoLABUseCase.runCatching {
                invoke(
                    pair.second,
                    pair.first.uid
                ).getOrThrow()!!.list.onEach { gameRecord ->
                    withContext(Dispatchers.Main) {
                        checkedGames.add(
                            Game(
                                roleId = gameRecord.gameRoleId,
                                type = HoYoLABGame.findByGameId(gameId = gameRecord.gameId),
                                region = gameRecord.region
                            )
                        )
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
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Lce.Loading
        )
    val cookie = _cookie.asStateFlow()
    val checkedGames = mutableStateListOf<Game>()

    @OptIn(ObsoleteCoroutinesApi::class)
    val tickPerSecond = ticker(delayMillis = 1000).receiveAsFlow()
        .map { ZonedDateTime.now() }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ZonedDateTime.now()
        )
    val hourOfDay = _hourOfDay.asStateFlow()
    val minute = _minute.asStateFlow()
    val insertAttendanceState = _insertAttendanceState.asStateFlow()

    fun setCookie(cookie: String) {
        _cookie.value = cookie
    }

    fun setHourOfDay(hourOfDay: Int) {
        _hourOfDay.value = hourOfDay
    }

    fun setMinute(minute: Int) {
        _minute.value = minute
    }

    fun createAttendance() {
        _insertAttendanceState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _insertAttendanceState.update {
                insertAttendanceUseCase.runCatching {
                    val hourOfDay = _hourOfDay.value
                    val minute = _minute.value
                    val attendance = Attendance(
                        cookie = _cookie.value,
                        nickname = _userInfo.value!!.nickname,
                        uid = _userInfo.value!!.uid,
                        hourOfDay = hourOfDay,
                        minute = minute,
                        timezoneId = ZoneId.systemDefault().id
                    )

                    with(attendance) {
                        copy(
                            id = invoke(attendance)
                        )
                    }
                }.mapCatching { attendance ->
                    val now = ZonedDateTime.now(ZoneId.of(attendance.timezoneId))

                    alarmScheduler.scheduleCheckInAlarm(
                        attendanceId = attendance.id,
                        hourOfDay = attendance.hourOfDay,
                        minute = attendance.minute
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

                    workManager.enqueueUniquePeriodicWork(
                        attendance.checkSessionWorkerName.toString(),
                        ExistingPeriodicWorkPolicy.UPDATE,
                        periodicCheckSessionWork
                    )

                    updateAttendanceUseCase(
                        attendance.copy(
                            checkSessionWorkerId = periodicCheckSessionWork.id
                        )
                    )

                    attendance.id
                }.mapCatching { attendanceId ->
                    insertGameUseCase(
                        *checkedGames.map {
                            it.copy(attendanceId = attendanceId)
                        }.toTypedArray()
                    )
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
}