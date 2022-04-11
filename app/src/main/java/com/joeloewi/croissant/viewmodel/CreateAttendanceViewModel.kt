package com.joeloewi.croissant.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.data.local.model.Attendance
import com.joeloewi.croissant.data.local.model.Game
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ObsoleteCoroutinesApi
@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val application: Application,
    private val hoYoLABService: HoYoLABService,
    private val croissantDatabase: CroissantDatabase
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])
    private val _createAttendanceState = MutableStateFlow<Lce<List<Long>>>(Lce.Content(listOf()))

    val cookie = _cookie
    private val userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            hoYoLABService.runCatching {
                getUserFullInfo(cookie).data!!.userInfo
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
            started = SharingStarted.WhileSubscribed(),
            initialValue = Lce.Loading
        )
    val connectedGames = userInfo
        .filter { it is Lce.Content }
        .combine(_cookie) { userInfo, cookie ->
            userInfo to cookie
        }.map { pair ->
            hoYoLABService.runCatching {
                getGameRecordCard(
                    pair.second,
                    pair.first.content!!.uid
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
    val checkedGames = mutableStateMapOf<HoYoLABGame, Boolean>()
    val tickerCalendar = ticker(delayMillis = 1000).receiveAsFlow()
        .map { Calendar.getInstance() }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
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
            _createAttendanceState.value = croissantDatabase.attendanceDao().runCatching {
                val hourOfDay = _hourOfDay.value
                val minute = _minute.value
                val attendance = Attendance(
                    cookie = _cookie.value,
                    nickname = userInfo.value.content!!.nickname,
                    uid = userInfo.value.content!!.uid,
                    hourOfDay = hourOfDay,
                    minute = minute
                )

                insert(attendance = attendance)
            }.mapCatching { attendanceId ->
                val attendance = croissantDatabase.attendanceDao().getOne(attendanceId).attendance
                val now = Calendar.getInstance()
                val canExecuteToday =
                    (now[Calendar.HOUR_OF_DAY] < attendance.hourOfDay) || (now[Calendar.HOUR_OF_DAY] == attendance.hourOfDay && now[Calendar.MINUTE] < attendance.minute)

                val targetTime = Calendar.getInstance().apply {
                    if (!canExecuteToday) {
                        add(Calendar.DATE, 1)
                    }

                    set(Calendar.HOUR_OF_DAY, attendance.hourOfDay)
                    set(Calendar.MINUTE, attendance.minute)
                }

                val periodicCheckSessionWork = PeriodicWorkRequest.Builder(
                    CheckSessionWorker::class.java,
                    6L,
                    TimeUnit.HOURS
                )
                    .setInputData(workDataOf(CheckSessionWorker.ATTENDANCE_ID to attendanceId))
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

                val periodicAttendanceCheckInEventWork = PeriodicWorkRequest.Builder(
                    AttendCheckInEventWorker::class.java,
                    24L,
                    TimeUnit.HOURS
                )
                    .setInitialDelay(
                        targetTime.timeInMillis - now.timeInMillis,
                        TimeUnit.MILLISECONDS
                    )
                    .setInputData(workDataOf(AttendCheckInEventWorker.ATTENDANCE_ID to attendanceId))
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

                croissantDatabase.attendanceDao().update(
                    attendance.copy(
                        attendCheckInEventWorkerId = periodicAttendanceCheckInEventWork.id,
                        checkSessionWorkerId = periodicCheckSessionWork.id
                    )
                )

                attendance.id
            }.mapCatching { attendanceId ->
                croissantDatabase.gameDao()
                    .insert(*checkedGames.filter { it.value }.map { hoYoLABGame ->
                        Game(
                            attendanceId = attendanceId,
                            name = hoYoLABGame.key
                        )
                    }.toTypedArray())
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