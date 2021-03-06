package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.receiver.AlarmReceiver
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.croissant.worker.CheckSessionWorker
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.GameUseCase
import com.joeloewi.domain.usecase.HoYoLABUseCase
import com.joeloewi.domain.wrapper.ContentOrError
import com.joeloewi.domain.wrapper.getOrThrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ObsoleteCoroutinesApi
@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val application: Application,
    private val alarmManager: AlarmManager,
    getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val insertAttendanceUseCase: AttendanceUseCase.Insert,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val insertGameUseCase: GameUseCase.Insert,
    private val getOneByUidAttendanceUseCase: AttendanceUseCase.GetOneByUid
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])
    private val _createAttendanceState = MutableStateFlow<Lce<List<Long>>>(Lce.Content(listOf()))
    private val _duplicatedAttendance = MutableStateFlow<Attendance?>(null)
    private val _userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            getUserFullInfoHoYoLABUseCase.runCatching {
                invoke(cookie = cookie).getOrThrow().data?.userInfo?.also {
                    getOneByUidAttendanceUseCase.runCatching {
                        invoke(it.uid)
                    }.onSuccess { attendance ->
                        _duplicatedAttendance.value = attendance
                    }
                }
            }.fold(
                onSuccess = {
                    ContentOrError.Content(it)
                },
                onFailure = {
                    ContentOrError.Error(it)
                }
            )
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ContentOrError.Content(null)
        )
    val connectedGames = _userInfo
        .combine(_cookie) { userInfo, cookie ->
            userInfo to cookie
        }.map { pair ->
            checkedGames.clear()
            getGameRecordCardHoYoLABUseCase.runCatching {
                pair.first.getOrThrow()?.let {
                    invoke(
                        pair.second,
                        it.uid
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
                }
            }.fold(
                onSuccess = {
                    if (it == null) {
                        Lce.Loading
                    } else {
                        Lce.Content(it)
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
    val cookie = _cookie.asStateFlow()
    val checkedGames = mutableStateListOf<Game>()
    val tickerCalendar = ticker(delayMillis = 1000).receiveAsFlow()
        .map { Calendar.getInstance() }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Calendar.getInstance()
        )
    val hourOfDay = _hourOfDay.asStateFlow()
    val minute = _minute.asStateFlow()
    val createAttendanceState = _createAttendanceState.asStateFlow()
    val duplicatedAttendance = _duplicatedAttendance.asStateFlow()

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

    fun createAttendance() {
        viewModelScope.launch(Dispatchers.IO) {
            _createAttendanceState.update { Lce.Loading }
            _createAttendanceState.update {
                insertAttendanceUseCase.runCatching {
                    val hourOfDay = _hourOfDay.value
                    val minute = _minute.value
                    val attendance = Attendance(
                        cookie = _cookie.value,
                        nickname = _userInfo.value.content!!.nickname,
                        uid = _userInfo.value.content!!.uid,
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
                        set(Calendar.SECOND, 30)
                    }

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
                            targetTime.timeInMillis,
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