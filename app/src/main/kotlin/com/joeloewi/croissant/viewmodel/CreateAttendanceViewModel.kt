package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.core.data.model.Game
import com.joeloewi.croissant.core.data.model.GameRecord
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.core.data.model.UserInfo
import com.joeloewi.croissant.domain.usecase.AttendanceUseCase
import com.joeloewi.croissant.domain.usecase.GameUseCase
import com.joeloewi.croissant.domain.usecase.HoYoLABUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.worker.CheckSessionWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val workManager: WorkManager,
    private val getUserFullInfoHoYoLABUseCase: HoYoLABUseCase.GetUserFullInfo,
    private val getGameRecordCardHoYoLABUseCase: HoYoLABUseCase.GetGameRecordCard,
    private val insertAttendanceUseCase: AttendanceUseCase.Insert,
    private val updateAttendanceUseCase: AttendanceUseCase.Update,
    private val insertGameUseCase: GameUseCase.Insert,
    private val getOneByUidAttendanceUseCase: AttendanceUseCase.GetOneByUid
) : ViewModel(),
    ContainerHost<CreateAttendanceViewModel.State, CreateAttendanceViewModel.SideEffect> {

    override val container: Container<State, SideEffect> = container(State())

    init {
        intent {
            container.stateFlow.mapLatest { it.cookie }
                .filter { it.isNotEmpty() }
                .distinctUntilChanged()
                .collect {
                    val userInfo = getUserFullInfoHoYoLABUseCase(cookie = it).getOrNull()
                    reduce { state.copy(userInfo = userInfo) }
                }
        }

        intent {
            container.stateFlow.mapLatest { it.userInfo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val existingAttendance = getOneByUidAttendanceUseCase(it.uid)
                    reduce { state.copy(existingAttendance = existingAttendance) }
                }
        }

        intent {
            container.stateFlow.mapLatest { it.userInfo }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val connectedGames = runCatching {
                        getGameRecordCardHoYoLABUseCase.invoke(
                            cookie = state.cookie,
                            uid = it.uid
                        ).getOrThrow()?.toImmutableList() ?: persistentListOf()
                    }.foldAsLce()

                    reduce { state.copy(connectedGames = connectedGames) }
                }
        }

        intent {
            container.stateFlow.mapLatest { it.connectedGames.content }
                .filterNotNull()
                .distinctUntilChanged()
                .collect {
                    val converted = it.map { gameRecord ->
                        Game(
                            roleId = gameRecord.gameRoleId,
                            type = HoYoLABGame.findByGameId(gameId = gameRecord.gameId),
                            region = gameRecord.region
                        )
                    }

                    withContext(Dispatchers.Main) {
                        state.checkedGames.clear()
                        state.checkedGames.addAll(converted)
                    }
                }
        }
    }

    fun setCookie(cookie: String) = intent { reduce { state.copy(cookie = cookie) } }

    fun setHourOfDay(hourOfDay: Int) = intent { reduce { state.copy(hourOfDay = hourOfDay) } }

    fun setMinute(minute: Int) = intent { reduce { state.copy(minute = minute) } }

    fun onNavigateUp() = intent { postSideEffect(SideEffect.NavigateUp) }

    fun onNavigateToAttendanceDetailScreen(attendanceId: Long) =
        intent { postSideEffect(SideEffect.NavigateToAttendanceDetail(attendanceId)) }

    fun onLoginHoYoLAB() = intent { postSideEffect(SideEffect.OnLoginHoYoLAB) }

    fun onShowCancelConfirmationDialog(showCancelConfirmationDialog: Boolean) =
        intent { reduce { state.copy(showCancelConfirmationDialog = showCancelConfirmationDialog) } }

    fun createAttendance() = intent {
        postSideEffect(SideEffect.ShowProgressDialog)
        val attendance = Attendance(
            cookie = state.cookie,
            nickname = state.userInfo?.nickname ?: "",
            uid = state.userInfo?.uid ?: 0,
            hourOfDay = state.hourOfDay,
            minute = state.minute,
            timezoneId = ZoneId.systemDefault().id
        )

        val attendanceId = insertAttendanceUseCase.invoke(attendance)

        alarmScheduler.scheduleCheckInAlarm(
            attendance = attendance,
            scheduleForTomorrow = false
        )

        val periodicCheckSessionWork = CheckSessionWorker.buildPeriodicWork(
            attendanceId = attendance.id
        )

        workManager.enqueueUniquePeriodicWork(
            attendance.checkSessionWorkerName.toString(),
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicCheckSessionWork
        )

        updateAttendanceUseCase(
            attendance.copy(
                checkSessionWorkerId = periodicCheckSessionWork.id
            )
        )

        insertGameUseCase(
            *state.checkedGames.map {
                it.copy(attendanceId = attendanceId)
            }.toTypedArray()
        )
        postSideEffect(SideEffect.DismissProgressDialog)
        postSideEffect(SideEffect.NavigateUp)
    }

    @Immutable
    data class State(
        val cookie: String = "",
        val hourOfDay: Int = ZonedDateTime.now().hour,
        val minute: Int = ZonedDateTime.now().minute,
        val checkedGames: SnapshotStateList<Game> = mutableStateListOf(),
        val userInfo: UserInfo? = null,
        val existingAttendance: Attendance? = null,
        val connectedGames: LCE<ImmutableList<GameRecord>> = LCE.Loading,
        val showCancelConfirmationDialog: Boolean = false
    )

    @Immutable
    sealed class SideEffect {
        data object ShowProgressDialog : SideEffect()
        data object DismissProgressDialog : SideEffect()
        data object NavigateUp : SideEffect()
        data class NavigateToAttendanceDetail(
            val attendanceId: Long
        ) : SideEffect()

        data object OnLoginHoYoLAB : SideEffect()
    }
}