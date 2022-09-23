package com.joeloewi.croissant.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.joeloewi.croissant.viewmodel.AttendanceLogsViewModel

@ExperimentalLifecycleComposeApi
@Stable
class AttendanceLogsState(
    val snackbarHostState: SnackbarHostState,
    private val navController: NavController,
    val attendanceLogsViewModel: AttendanceLogsViewModel
) {
    //state
    val previousBackStackEntry
        @Composable get() = navController.previousBackStackEntry
    val pagedAttendanceLogs
        @Composable get() = attendanceLogsViewModel.pagedAttendanceLogs.collectAsLazyPagingItems()
    val deleteAllState
        @Composable get() = attendanceLogsViewModel.deleteAllState.collectAsStateWithLifecycle().value
    var isShowingDeleteConfirmationDialog by mutableStateOf(false)
        private set

    fun onNavigateUp() {
        navController.navigateUp()
    }

    fun onDeleteAll() {
        showDeleteConfirmationDialog(false)
        attendanceLogsViewModel.deleteAll()
    }

    fun showDeleteConfirmationDialog(isShowing: Boolean) {
        isShowingDeleteConfirmationDialog = isShowing
    }
}

@ExperimentalLifecycleComposeApi
@Composable
fun rememberAttendanceLogsState(
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    navController: NavController,
    attendanceLogsViewModel: AttendanceLogsViewModel
) = remember(
    snackbarHostState,
    navController,
    attendanceLogsViewModel
) {
    AttendanceLogsState(
        snackbarHostState = snackbarHostState,
        navController = navController,
        attendanceLogsViewModel = attendanceLogsViewModel
    )
}