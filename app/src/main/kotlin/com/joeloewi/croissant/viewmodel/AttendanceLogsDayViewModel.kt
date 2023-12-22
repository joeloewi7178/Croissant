package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsDayViewModel @Inject constructor(
    getByDatePagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetByDatePaged,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsDayScreen.ATTENDANCE_ID
    private val _loggableWorkerKey = AttendancesDestination.AttendanceLogsDayScreen.LOGGABLE_WORKER
    private val _localDateKey = AttendancesDestination.AttendanceLogsDayScreen.LOCAL_DATE

    private val _attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    private val _loggableWorker =
        savedStateHandle.get<LoggableWorker>(_loggableWorkerKey) ?: LoggableWorker.UNKNOWN
    private val _localDate = savedStateHandle.get<String>(_localDateKey) ?: LocalDate.now().format(
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    val pagedAttendanceLogs = getByDatePagedWorkerExecutionLogUseCase(
        attendanceId = _attendanceId,
        loggableWorker = _loggableWorker,
        localDate = _localDate
    ).cachedIn(viewModelScope)
}