package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.receiver.AlarmReceiver
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val application: Application,
    private val alarmManager: AlarmManager,
    getAllPagedAttendanceWithGamesUseCase: AttendanceUseCase.GetAllPaged,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete,
) : ViewModel() {
    val pagedAttendanceWithGames = getAllPagedAttendanceWithGamesUseCase().cachedIn(viewModelScope)

    fun deleteAttendance(attendance: Attendance) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                listOf(
                    attendance.checkSessionWorkerName,
                    attendance.attendCheckInEventWorkerName,
                    attendance.oneTimeAttendCheckInEventWorkerName
                ).map { it.toString() }.map { uniqueWorkName ->
                    WorkManager.getInstance(application).cancelUniqueWork(uniqueWorkName)
                }

                val alarmPendingIntent = PendingIntent.getBroadcast(
                    application,
                    attendance.id.toInt(),
                    Intent(application, AlarmReceiver::class.java).apply {
                        action = AlarmReceiver.RECEIVE_ATTEND_CHECK_IN_ALARM
                        putExtra(AlarmReceiver.ATTENDANCE_ID, attendance.id)
                    },
                    pendingIntentFlagUpdateCurrent
                )

                alarmManager.cancel(
                    alarmPendingIntent
                )

                deleteAttendanceUseCase(attendance)
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}