package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.usecase.ResultCountUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsCalendarViewModel @Inject constructor(
    private val deleteAllPagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.DeleteAll,
    getStartToEndWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetStartToEnd,
    getAllResultCountUseCase: ResultCountUseCase.GetAll,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsCalendarScreen.ATTENDANCE_ID
    private val _loggableWorkerKey =
        AttendancesDestination.AttendanceLogsCalendarScreen.LOGGABLE_WORKER
    private val _deleteAllState = MutableStateFlow<Lce<Int>>(Lce.Content(-1))

    val loggableWorker =
        savedStateHandle.getStateFlow(_loggableWorkerKey, LoggableWorker.UNKNOWN)
    val deleteAllState = _deleteAllState.asStateFlow()
    val attendanceId = savedStateHandle.get<Long>(_attendanceIdKey) ?: Long.MIN_VALUE
    val resultCounts = loggableWorker.flatMapLatest {
        getAllResultCountUseCase(it)
    }.map {
        it.toImmutableList()
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = persistentListOf()
    )
    val startToEnd = getStartToEndWorkerExecutionLogUseCase().map {
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(it.first),
            ZoneId.systemDefault()
        ) to ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.second), ZoneId.systemDefault())
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = with(ZonedDateTime.now()) {
            withDayOfMonth(1) to withDayOfMonth(
                Year.of(year).atMonth(month).atEndOfMonth().dayOfMonth
            )
        }
    )

    fun deleteAll() {
        _deleteAllState.value = Lce.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _deleteAllState.value = deleteAllPagedWorkerExecutionLogUseCase.runCatching {
                invoke(
                    attendanceId = attendanceId,
                    loggableWorker = loggableWorker.value
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