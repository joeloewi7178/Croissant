package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.usecase.WorkerExecutionLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsViewModel @Inject constructor(
    getAllPagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetAllPaged,
    private val deleteAllPagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.DeleteAll,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsScreen.ATTENDANCE_ID
    private val _loggableWorkerKey = AttendancesDestination.AttendanceLogsScreen.CROISSANT_WORKER
    private val _attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    private val _loggableWorker =
        savedStateHandle.get<LoggableWorker>(_loggableWorkerKey) ?: LoggableWorker.UNKNOWN

    val pagedAttendanceLogs = getAllPagedWorkerExecutionLogUseCase(
        attendanceId = _attendanceId,
        loggableWorker = _loggableWorker
    ).cachedIn(viewModelScope)

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAllPagedWorkerExecutionLogUseCase(
                attendanceId = _attendanceId,
                loggableWorker = _loggableWorker
            )
        }
    }
}