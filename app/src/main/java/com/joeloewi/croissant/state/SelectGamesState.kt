package com.joeloewi.croissant.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.joeloewi.domain.common.HoYoLABGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalLifecycleComposeApi
@ObsoleteCoroutinesApi
@Stable
class SelectGamesState(
    val snackbarHostState: SnackbarHostState,
    val supportedGames: ImmutableList<HoYoLABGame>,
    private val createAttendanceState: CreateAttendanceState
) {
    val duplicatedAttendance
        @Composable get() = createAttendanceState.duplicatedAttendance
    val checkedGames
        get() = createAttendanceState.checkedGames
    val connectedGames
        @Composable get() = createAttendanceState.connectedGames
    val noGamesSelected
        get() = derivedStateOf { checkedGames.isEmpty() }.value

    fun isSupportedGame(gameId: Int): Boolean =
        supportedGames.contains(HoYoLABGame.findByGameId(gameId))

    fun onNextButtonClick() {
        createAttendanceState.onNextButtonClick()
    }

    fun onNavigateToAttendanceDetailScreen(attendanceId: Long) {
        createAttendanceState.onNavigateToAttendanceDetailScreen(attendanceId)
    }

    fun onCancelCreateAttendance() {
        createAttendanceState.onCancelCreateAttendance()
    }
}

@ExperimentalLifecycleComposeApi
@ObsoleteCoroutinesApi
@Composable
fun rememberSelectGamesState(
    createAttendanceState: CreateAttendanceState,
    supportedGames: ImmutableList<HoYoLABGame>,
    snackbarHostState: SnackbarHostState = SnackbarHostState()
) = remember(
    createAttendanceState,
    supportedGames,
    snackbarHostState
) {
    SelectGamesState(
        createAttendanceState = createAttendanceState,
        supportedGames = supportedGames,
        snackbarHostState = snackbarHostState
    )
}