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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
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
    ContainerHost<AttendanceLogsCalendarViewModel.State, AttendanceLogsCalendarViewModel.SideEffect> {
    //parameter
    private val _attendanceIdKey = AttendancesDestination.AttendanceLogsCalendarScreen.ATTENDANCE_ID
    private val _loggableWorkerKey =
        AttendancesDestination.AttendanceLogsCalendarScreen.LOGGABLE_WORKER

    override val container: Container<State, SideEffect> =
        container(State()) {
            intent {
                getAllResultCountUseCase(state.attendanceId, state.loggableWorker).map {
                    it.toImmutableList()
                }.flowOn(Dispatchers.IO).stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Eagerly,
                    initialValue = persistentListOf()
                ).collect {
                    reduce { state.copy(resultCounts = it) }
                }
            }

            intent {
                getStartToEnd(state.attendanceId, state.loggableWorker).map {
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(it.start),
                        ZoneId.systemDefault()
                    ) to ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(it.end),
                        ZoneId.systemDefault()
                    )
                }.flowOn(Dispatchers.IO).stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = with(ZonedDateTime.now()) {
                        withDayOfMonth(1) to withDayOfMonth(
                            Year.of(year).atMonth(month).atEndOfMonth().dayOfMonth
                        )
                    }
                ).collect {
                    reduce { state.copy(startToEnd = it) }
                }
            }

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
        }

    fun deleteAll() {
        intent {
            postSideEffect(SideEffect.ShowLoadingDialog)
            val count = deleteAllPagedWorkerExecutionLogUseCase.invoke(
                state.attendanceId,
                state.loggableWorker
            )
            postSideEffect(SideEffect.HideLoadingDialog)
            postSideEffect(SideEffect.ShowDeleteCompleteSnackbar(count))
        }
    }

    fun onClickDay(localDate: String) {
        intent {
            postSideEffect(
                SideEffect.NavigateToDay(
                    state.attendanceId,
                    state.loggableWorker,
                    localDate
                )
            )
        }
    }

    fun onShowConfirmDeleteDialogChange(showConfirmDeleteDialog: Boolean) {
        intent {
            reduce { state.copy(showConfirmDeleteDialog = showConfirmDeleteDialog) }
        }
    }

    data class State(
        val resultCounts: ImmutableList<ResultCount> = persistentListOf(),
        val startToEnd: Pair<ZonedDateTime, ZonedDateTime> = ZonedDateTime.now() to ZonedDateTime.now(),
        val attendanceId: Long = Long.MIN_VALUE,
        val loggableWorker: LoggableWorker = LoggableWorker.UNKNOWN,
        val showConfirmDeleteDialog: Boolean = false
    )

    sealed class SideEffect {
        data class NavigateToDay(
            val attendanceId: Long,
            val loggableWorker: LoggableWorker,
            val localDate: String
        ) : SideEffect()

        data class ShowDeleteCompleteSnackbar(
            val count: Int
        ) : SideEffect()

        data object ShowLoadingDialog : SideEffect()
        data object HideLoadingDialog : SideEffect()
    }
}