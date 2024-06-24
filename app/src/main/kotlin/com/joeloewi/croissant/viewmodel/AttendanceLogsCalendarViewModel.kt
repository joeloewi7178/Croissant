package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.core.data.model.ResultCount
import com.joeloewi.croissant.domain.usecase.ResultCountUseCase
import com.joeloewi.croissant.domain.usecase.ResultRangeUseCase
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsCalendarViewModel @Inject constructor(
    private val deleteAllPagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.DeleteAll,
    private val getStartToEnd: ResultRangeUseCase.GetStartToEnd,
    private val getAllResultCountUseCase: ResultCountUseCase.GetAll,
    savedStateHandle: SavedStateHandle
) : ViewModel(),
    ContainerHost<AttendanceLogsCalendarViewModel.AttendanceLogsCalendarState, AttendanceLogsCalendarViewModel.AttendanceLogsCalendarSideEffect> {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsCalendarScreen.ATTENDANCE_ID
    private val _loggableWorkerKey =
        AttendancesDestination.AttendanceLogsCalendarScreen.LOGGABLE_WORKER
    private val _loggableWorker =
        savedStateHandle.getStateFlow(_loggableWorkerKey, LoggableWorker.UNKNOWN)
    private val _attendanceId =
        savedStateHandle.getStateFlow(_attendanceIdKey, Long.MIN_VALUE)
    private val _resultCounts =
        combine(_attendanceId, _loggableWorker) { attendanceId, loggableWorker ->
            getAllResultCountUseCase(attendanceId, loggableWorker)
        }.flatMapLatest { it }.map {
            it.toImmutableList()
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = persistentListOf()
        )
    private val _startToEnd =
        combine(_attendanceId, _loggableWorker) { attendanceId, loggableWorker ->
            getStartToEnd(attendanceId, loggableWorker)
        }.flatMapLatest { it }.map {
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(it.start),
                ZoneId.systemDefault()
            ) to ZonedDateTime.ofInstant(Instant.ofEpochMilli(it.end), ZoneId.systemDefault())
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = with(ZonedDateTime.now()) {
                withDayOfMonth(1) to withDayOfMonth(
                    Year.of(year).atMonth(month).atEndOfMonth().dayOfMonth
                )
            }
        )

    override val container: Container<AttendanceLogsCalendarState, AttendanceLogsCalendarSideEffect> =
        container(AttendanceLogsCalendarState()) {
            intent {
                _resultCounts.collect {
                    reduce { state.copy(resultCounts = it) }
                }
            }

            intent {
                _startToEnd.collect {
                    reduce { state.copy(startToEnd = it) }
                }
            }
        }

    fun deleteAll() {
        intent {
            postSideEffect(AttendanceLogsCalendarSideEffect.Dialog(true))

            postSideEffect(AttendanceLogsCalendarSideEffect.Dialog(false))
        }
    }

    data class AttendanceLogsCalendarState(
        val resultCounts: ImmutableList<ResultCount> = persistentListOf(),
        val startToEnd: Pair<ZonedDateTime, ZonedDateTime> = ZonedDateTime.now() to ZonedDateTime.now()
    )

    sealed class AttendanceLogsCalendarSideEffect {
        data class Dialog(
            val shouldShow: Boolean
        ) : AttendanceLogsCalendarSideEffect()
    }
}