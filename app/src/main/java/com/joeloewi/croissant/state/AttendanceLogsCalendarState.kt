package com.joeloewi.croissant.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import java.time.Month
import java.time.Year
import java.time.YearMonth

@ExperimentalPagerApi
@ExperimentalLifecycleComposeApi
@Stable
class AttendanceLogsCalendarState(
    val snackbarHostState: SnackbarHostState,
    val pagerState: PagerState,
    private val navController: NavController,
    val attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel
) {
    //state
    val previousBackStackEntry
        get() = navController.previousBackStackEntry
    val deleteAllState
        @Composable get() = attendanceLogsCalendarViewModel.deleteAllState.collectAsStateWithLifecycle().value
    val year: Year
        @Composable get() = attendanceLogsCalendarViewModel.year.collectAsStateWithLifecycle().value
    var isShowingDeleteConfirmationDialog by mutableStateOf(false)
        private set

    fun getCountByDate(
        year: Year,
        month: Month,
        day: Int
    ) = attendanceLogsCalendarViewModel.getCountByDate(year, month, day)

    fun onNavigateUp() {
        navController.navigateUp()
    }

    fun onDeleteAll() {
        showDeleteConfirmationDialog(false)
        attendanceLogsCalendarViewModel.deleteAll()
    }

    fun onClickDay(
        localDate: String
    ) {
        navController.navigate(
            AttendancesDestination.AttendanceLogsDayScreen().generateRoute(
                attendanceId = attendanceLogsCalendarViewModel.attendanceId,
                loggableWorker = attendanceLogsCalendarViewModel.loggableWorker,
                localDate = localDate,
            )
        )
    }

    fun showDeleteConfirmationDialog(isShowing: Boolean) {
        isShowingDeleteConfirmationDialog = isShowing
    }

    fun setYear(year: Year) {
        attendanceLogsCalendarViewModel.setYear(year)
    }
}

@ExperimentalPagerApi
@ExperimentalLifecycleComposeApi
@Composable
fun rememberAttendanceLogsCalendarState(
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    pagerState: PagerState = rememberPagerState(initialPage = YearMonth.now().monthValue - 1),
    navController: NavController,
    attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel,
) = remember(
    snackbarHostState,
    pagerState,
    navController,
    attendanceLogsCalendarViewModel
) {
    AttendanceLogsCalendarState(
        snackbarHostState = snackbarHostState,
        pagerState = pagerState,
        navController = navController,
        attendanceLogsCalendarViewModel = attendanceLogsCalendarViewModel
    )
}