package com.joeloewi.croissant.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import com.joeloewi.croissant.receiver.AlarmReceiver
import com.joeloewi.croissant.util.pendingIntentFlagUpdateCurrent
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.usecase.AttendanceUseCase
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val application: Application,
    getAllPagedAttendanceWithGamesUseCase: AttendanceUseCase.GetAllPaged,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete,
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val setNotifyMigrateToAlarmManagerUseCase: SettingsUseCase.SetNotifyMigrateToAlarmManager
) : ViewModel() {
    val pagedAttendanceWithGames = getAllPagedAttendanceWithGamesUseCase().cachedIn(viewModelScope)
    val notifyMigrateToAlarmManager =
        getSettingsUseCase().map { it.notifyMigrateToAlarmManager }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = true
        )

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

                (application.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(
                    alarmPendingIntent
                )

                deleteAttendanceUseCase(attendance)
            }.onSuccess {

            }.onFailure {

            }
        }
    }

    fun setNotifyMigrateToAlarmManager(notifyMigrateToAlarmManager: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            setNotifyMigrateToAlarmManagerUseCase(notifyMigrateToAlarmManager)
        }
    }
}