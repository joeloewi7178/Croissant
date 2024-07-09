package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.joeloewi.croissant.core.data.model.LoggableWorker
import com.joeloewi.croissant.domain.usecase.WorkerExecutionLogUseCase
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapConcat
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AttendanceLogsDayViewModel @Inject constructor(
    getByDatePagedWorkerExecutionLogUseCase: WorkerExecutionLogUseCase.GetByDatePaged,
    savedStateHandle: SavedStateHandle,
) : ViewModel(),
    ContainerHost<AttendanceLogsDayViewModel.State, AttendanceLogsDayViewModel.SideEffect> {

    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsDayScreen.ATTENDANCE_ID
    private val _loggableWorkerKey = AttendancesDestination.AttendanceLogsDayScreen.LOGGABLE_WORKER
    private val _localDateKey = AttendancesDestination.AttendanceLogsDayScreen.LOCAL_DATE

    override val container: Container<State, SideEffect> = container(State()) {

        intent {
            savedStateHandle.getStateFlow(_attendanceIdKey, Long.MIN_VALUE).collect {
                reduce { state.copy(attendanceId = it) }
            }
        }

        intent {
            savedStateHandle.getStateFlow(_loggableWorkerKey, LoggableWorker.UNKNOWN).collect {
                reduce { state.copy(loggableWorker = it) }
            }
        }

        intent {
            savedStateHandle.getStateFlow(
                _localDateKey,
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            ).collect {
                reduce { state.copy(localDate = it) }
            }
        }
    }

    val pagedAttendanceLogs = container.stateFlow.flatMapConcat {
        getByDatePagedWorkerExecutionLogUseCase(it.attendanceId, it.loggableWorker, it.localDate)
    }.cachedIn(viewModelScope)

    fun onNavigateUp() {
        intent {
            postSideEffect(SideEffect.NavigateUp)
        }
    }

    data class State(
        val attendanceId: Long = Long.MIN_VALUE,
        val loggableWorker: LoggableWorker = LoggableWorker.UNKNOWN,
        val localDate: String = LocalDate.now().toString()
    )

    sealed class SideEffect {
        data object NavigateUp : SideEffect()
    }
}