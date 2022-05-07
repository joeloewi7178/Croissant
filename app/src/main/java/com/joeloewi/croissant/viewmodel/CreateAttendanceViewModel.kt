package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.croissant.worker.CheckSessionWorker
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.entity.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.GameUseCase
import com.joeloewi.domain.usecase.HoYoLABUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ObsoleteCoroutinesApi
@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val application: Application,
    getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val insertAttendanceUseCase: AttendanceUseCase.Insert,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val insertGameUseCase: GameUseCase.Insert
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])
    private val _createAttendanceState = MutableStateFlow<Lce<List<Long>>>(Lce.Content(listOf()))

    val cookie = _cookie
    private val userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            getUserFullInfoHoYoLABUseCase.runCatching {
                invoke(cookie = cookie).data!!.userInfo
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
    val connectedGames = userInfo
        .filter { it is Lce.Content }
        .combine(_cookie) { userInfo, cookie ->
            userInfo to cookie
        }.map { pair ->
            getGameRecordCardHoYoLABUseCase.runCatching {
                invoke(
                    pair.second,
                    pair.first.content!!.uid
                )!!
            }.fold(
                onSuccess = { gameRecordCardData ->
                    Lce.Content(gameRecordCardData.list)
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

    fun setCookie(cookie: String) {
        viewModelScope.launch {
            _cookie.emit(cookie)
        }
    }

    fun setHourOfDay(hourOfDay: Int) {
        _hourOfDay.value = hourOfDay
    }

    fun setMinute(minute: Int) {
        _minute.value = minute
    }

    fun createAttendance() {
        _createAttendanceState.value = Lce.Loading

        viewModelScope.launch(Dispatchers.IO) {
            _createAttendanceState.value = insertAttendanceUseCase.runCatching {
                val hourOfDay = _hourOfDay.value
                val minute = _minute.value
                val attendance = Attendance(
                    cookie = _cookie.value,
                    nickname = userInfo.value.content!!.nickname,
                    uid = userInfo.value.content!!.uid,
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
                        attendCheckInEventWorkerId = periodicAttendanceCheckInEventWork.id,
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