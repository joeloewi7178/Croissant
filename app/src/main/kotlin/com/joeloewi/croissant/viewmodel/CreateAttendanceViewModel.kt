package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.GameUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsILCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
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
    private val _insertAttendanceState = MutableStateFlow<ILCE<List<Long>>>(ILCE.Idle)
    private val _userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            getUserFullInfoHoYoLABUseCase(cookie = cookie).getOrThrow().data?.userInfo
        }
        .flowOn(Dispatchers.IO)
        .catch {}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    val duplicatedAttendance = _userInfo
        .filterNotNull()
        .map { getOneByUidAttendanceUseCase(it.uid) }
        .flowOn(Dispatchers.IO)
        .catch {}
        .stateIn(
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
                ).getOrThrow()!!.list.also { list ->
                    list.map { gameRecord ->
                        Game(
                            roleId = gameRecord.gameRoleId,
                            type = HoYoLABGame.findByGameId(gameId = gameRecord.gameId),
                            region = gameRecord.region
                        )
                    }.let { withContext(Dispatchers.Main) { checkedGames.addAll(it) } }
                }
            }.foldAsLce()
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = LCE.Loading
        )
    val cookie = _cookie.asStateFlow()
    val checkedGames = mutableStateListOf<Game>()
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
        _insertAttendanceState.value = ILCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _insertAttendanceState.value = insertAttendanceUseCase.runCatching {
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
                alarmScheduler.scheduleCheckInAlarm(
                    attendance = attendance,
                    scheduleForTomorrow = false
                )

                val periodicCheckSessionWork = CheckSessionWorker.buildPeriodicWork(
                    attendanceId = attendance.id
                )

                workManager.enqueueUniquePeriodicWork(
                    attendance.checkSessionWorkerName.toString(),
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
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
            }.foldAsILCE()
        }
    }
}