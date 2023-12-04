package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.joeloewi.croissant.viewmodel.AttendanceLogsDayViewModel
import kotlinx.coroutines.Dispatchers

@Stable
class AttendanceLogsDayState(
    private val navController: NavHostController,
    private val attendanceLogsDayViewModel: AttendanceLogsDayViewModel
) {
    val pagedAttendanceLogs
        @Composable
        get() = attendanceLogsDayViewModel.pagedAttendanceLogs.collectAsLazyPagingItems(Dispatchers.IO)

    val previousBackStackEntry
        get() = navController.previousBackStackEntry

    fun onNavigateUp() {
        navController.navigateUp()
    }
}

@Composable
fun rememberAttendanceLogsDayState(
    navController: NavHostController,
    attendanceLogsDayViewModel: AttendanceLogsDayViewModel
) = remember(
    navController,
    attendanceLogsDayViewModel
) {
    AttendanceLogsDayState(
        navController = navController,
        attendanceLogsDayViewModel = attendanceLogsDayViewModel
    )
}