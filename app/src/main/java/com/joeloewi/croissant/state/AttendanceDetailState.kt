package com.joeloewi.croissant.state

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel
import com.joeloewi.domain.common.LoggableWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalLifecycleComposeApi
@Stable
class AttendanceDetailState(
    val snackbarHostState: SnackbarHostState,
    val context: Context,
    val coroutineScope: CoroutineScope,
    private val attendanceDetailViewModel: AttendanceDetailViewModel,
    val navController: NavController
) {
    //state
    private val attendanceWithGames
        @Composable get() = attendanceDetailViewModel.attendanceWithGamesState.collectAsStateWithLifecycle().value
    val hourOfDay
        @Composable get() = attendanceDetailViewModel.hourOfDay.collectAsStateWithLifecycle().value
    val minute
        @Composable get() = attendanceDetailViewModel.minute.collectAsStateWithLifecycle().value
    val nickname
        @Composable get() = attendanceDetailViewModel.nickname.collectAsStateWithLifecycle().value
    val uid
        @Composable get() = attendanceDetailViewModel.uid.collectAsStateWithLifecycle().value
    private val updateAttendanceState
        @Composable get() = attendanceDetailViewModel.updateAttendanceState.collectAsStateWithLifecycle().value
    val checkSessionWorkerSuccessLogCount
        @Composable get() = attendanceDetailViewModel.checkSessionWorkerSuccessLogCount.collectAsStateWithLifecycle().value
    val checkSessionWorkerFailureLogCount
        @Composable get() = attendanceDetailViewModel.checkSessionWorkerFailureLogCount.collectAsStateWithLifecycle().value
    val attendCheckInEventWorkerSuccessLogCount
        @Composable get() = attendanceDetailViewModel.attendCheckInEventWorkerSuccessLogCount.collectAsStateWithLifecycle().value
    val attendCheckInEventWorkerFailureLogCount
        @Composable get() = attendanceDetailViewModel.attendCheckInEventWorkerFailureLogCount.collectAsStateWithLifecycle().value
    val previousBackStackEntry
        get() = navController.previousBackStackEntry

    //state list
    val checkedGames
        get() = attendanceDetailViewModel.checkedGames

    //calculated from state
    val isProgressDialogShowing
        @Composable get() = updateAttendanceState.isLoading
    val isNavigateUpRequested
        @Composable get() = updateAttendanceState.content != null
    val isSuccessfullyLoaded
        @Composable get() = attendanceWithGames is Lce.Content
    val hasExecutedAtLeastOnce
        @Composable get() = attendCheckInEventWorkerSuccessLogCount > 0 || attendCheckInEventWorkerFailureLogCount > 0

    //function
    fun onHourOfDayChange(hourOfDay: Int) {
        attendanceDetailViewModel.setHourOfDay(hourOfDay)
    }

    fun onMinuteChange(minute: Int) {
        attendanceDetailViewModel.setMinute(minute)
    }

    fun onNavigateUp() {
        navController.navigateUp()
    }

    fun onClickLogSummary(loggableWorker: LoggableWorker) {
        navController.navigate(
            AttendancesDestination.AttendanceLogsScreen().generateRoute(
                attendanceDetailViewModel.attendanceId,
                loggableWorker
            )
        )
    }

    fun onClickRefreshSession() {
        navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
    }

    fun onClickSave() {
        if (checkedGames.isEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.select_at_least_one_game))
            }
        } else {
            attendanceDetailViewModel.updateAttendance()
        }
    }
}

@ExperimentalLifecycleComposeApi
@Composable
fun rememberAttendanceDetailState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    attendanceDetailViewModel: AttendanceDetailViewModel,
    navController: NavController
) = remember(
    snackbarHostState,
    coroutineScope,
    attendanceDetailViewModel,
    navController
) {
    AttendanceDetailState(
        snackbarHostState = snackbarHostState,
        context = context,
        coroutineScope = coroutineScope,
        attendanceDetailViewModel = attendanceDetailViewModel,
        navController = navController
    )
}
