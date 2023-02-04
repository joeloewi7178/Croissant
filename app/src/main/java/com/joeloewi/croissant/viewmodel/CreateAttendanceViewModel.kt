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
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.CheckSessionWorker
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.GameUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val application: Application,
    private val alarmManager: AlarmManager,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val insertAttendanceUseCase: AttendanceUseCase.Insert,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val insertGameUseCase: GameUseCase.Insert,
    private val getOneByUidAttendanceUseCase: AttendanceUseCase.GetOneByUid,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _pageIndexKey = "pageIndex"
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
    val pageIndex = savedStateHandle.getStateFlow(_pageIndexKey, 0)
    val currentPageIndex
        get() = pageIndex.value

    fun setPageIndex(pageIndex: Int) {
        savedStateHandle[_pageIndexKey] = pageIndex
    }

    fun setCookie(cookie: String) {
        _cookie.update { cookie }
    }

    fun setHourOfDay(hourOfDay: Int) {
        _hourOfDay.update { hourOfDay }
    }

    fun setMinute(minute: Int) {
        _minute.update { minute }
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
                    val canExecuteToday =
                        (now.hour < attendance.hourOfDay) || (now.hour == attendance.hourOfDay && now.minute < attendance.minute)

                    val targetTime = ZonedDateTime.now(ZoneId.of(attendance.timezoneId))
                        .plusDays(
                            if (!canExecuteToday) {
                                1
                            } else {
                                0
                            }
                        )
                        .withHour(attendance.hourOfDay)
                        .withMinute(attendance.minute)
                        .withSecond(30)

                    val alarmIntent = Intent(application, AlarmReceiver::class.java).apply {
                        action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
                        putExtra(AlarmReceiver.ATTENDANCE_ID, attendance.id)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        application,
                        attendance.id.toInt(),
                        alarmIntent,
                        pendingIntentFlagUpdateCurrent
                    )

                    with(alarmManager) {
                        cancel(pendingIntent)
                        AlarmManagerCompat.setExactAndAllowWhileIdle(
                            this,
                            AlarmManager.RTC_WAKEUP,
                            targetTime.toInstant().toEpochMilli(),
                            pendingIntent
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