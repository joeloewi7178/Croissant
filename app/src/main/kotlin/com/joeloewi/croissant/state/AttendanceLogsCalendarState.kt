package com.joeloewi.croissant.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import java.time.Month
import java.time.Year
import java.time.YearMonth

@OptIn(ExperimentalFoundationApi::class)
@Stable
class AttendanceLogsCalendarState(
    val snackbarHostState: SnackbarHostState,
    val pagerState: PagerState,
    private val navController: NavHostController,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberAttendanceLogsCalendarState(
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    pagerState: PagerState = rememberPagerState(
        initialPage = YearMonth.now().monthValue - 1
    ) {
        Month.entries.size
    },
    navController: NavHostController,
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