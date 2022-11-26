package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.usecase.WorkerExecutionLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsCalendarViewModel @Inject constructor(
    private val getCountByStateAndDateWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetCountByStateAndDate,
    private val deleteAllPagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.DeleteAll,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsCalendarScreen.ATTENDANCE_ID
    private val _loggableWorkerKey =
        AttendancesDestination.AttendanceLogsCalendarScreen.LOGGABLE_WORKER
    private val _year = MutableStateFlow(Year.now())

    private val _deleteAllState = MutableStateFlow<Lce<Int>>(Lce.Content(-1))
    val deleteAllState = _deleteAllState.asStateFlow()

    val attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    val loggableWorker =
        savedStateHandle.get<LoggableWorker>(_loggableWorkerKey) ?: LoggableWorker.UNKNOWN
    val year = _year.asStateFlow()

    fun deleteAll() {
        _deleteAllState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _deleteAllState.update {
                deleteAllPagedWorkerExecutionLogUseCase.runCatching {
                    invoke(
                        attendanceId = attendanceId,
                        loggableWorker = loggableWorker
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

    fun getCountByDate(
        year: Year,
        month: Month,
        day: Int
    ): Flow<Pair<Long, Long>> = combine(
        getCountByStateAndDateWorkerExecutionLogUseCase(
            attendanceId,
            loggableWorker,
            WorkerExecutionLogState.SUCCESS,
            year.atMonth(month).atDay(day).format(DateTimeFormatter.ISO_LOCAL_DATE)
        ), getCountByStateAndDateWorkerExecutionLogUseCase(
            attendanceId,
            loggableWorker,
            WorkerExecutionLogState.FAILURE,
            year.atMonth(month).atDay(day).format(DateTimeFormatter.ISO_LOCAL_DATE)
        )
    ) { success, failure -> success to failure }.flowOn(Dispatchers.IO).catch { }

    fun setYear(year: Year) {
        _year.value = year
    }
}