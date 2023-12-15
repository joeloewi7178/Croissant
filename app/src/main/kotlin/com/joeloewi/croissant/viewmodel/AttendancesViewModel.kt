package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.util.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val alarmScheduler: AlarmScheduler,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete,
    getAllPagedAttendanceWithGamesUseCase: AttendanceUseCase.GetAllPaged,
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
                    workManager.cancelUniqueWork(uniqueWorkName)
                }

                alarmScheduler.cancelCheckInAlarm(attendance.id)

                deleteAttendanceUseCase(attendance)
            }.onSuccess {

            }.onFailure {

            }
        }
    }
}