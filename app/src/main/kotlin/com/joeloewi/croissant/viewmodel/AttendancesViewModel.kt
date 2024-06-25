package com.joeloewi.croissant.viewmodel

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.util.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AttendancesViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val alarmScheduler: AlarmScheduler,
    private val deleteAttendanceUseCase: AttendanceUseCase.Delete,
    getAllPagedAttendanceWithGamesUseCase: AttendanceUseCase.GetAllPaged,
) : ViewModel(), ContainerHost<Unit, AttendancesViewModel.SideEffect> {
    val pagedAttendanceWithGames = getAllPagedAttendanceWithGamesUseCase().cachedIn(viewModelScope)

    override val container: Container<Unit, SideEffect> = container(Unit)

    fun deleteAttendance(attendance: Attendance) {
        intent {
            Firebase.analytics.logEvent("delete_attendance", bundleOf())

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
                if (it is CancellationException) {
                    throw it
                }
            }
        }
    }

    fun onClickAttendance(attendanceId: Long) {
        intent {
            postSideEffect(SideEffect.OnClickAttendance(attendanceId))
        }
    }

    fun onClickCreateAttendance() {
        intent {
            postSideEffect(SideEffect.OnClickCreateAttendance)
        }
    }

    sealed class SideEffect {
        data class OnClickAttendance(
            val attendanceId: Long
        ) : SideEffect()

        data object OnClickCreateAttendance : SideEffect()
    }
}