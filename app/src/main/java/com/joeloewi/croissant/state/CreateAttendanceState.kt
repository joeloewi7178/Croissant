package com.joeloewi.croissant.state

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.ZonedDateTime

@Stable
class CreateAttendanceState(
    private val navController: NavHostController,
    private val createAttendanceViewModel: CreateAttendanceViewModel,
    val pages: ImmutableList<CreateAttendancePage>
) {
    val previousBackStackEntry
        get() = navController.previousBackStackEntry
    val cookie
        @Composable get() = createAttendanceViewModel.cookie.collectAsStateWithLifecycle().value
    val connectedGames
        @Composable get() = createAttendanceViewModel.connectedGames.collectAsStateWithLifecycle().value
    val insertAttendanceState
        @Composable get() = createAttendanceViewModel.insertAttendanceState.collectAsStateWithLifecycle().value
    val checkedGames
        get() = createAttendanceViewModel.checkedGames
    val duplicatedAttendance
        @Composable get() = createAttendanceViewModel.duplicatedAttendance.collectAsStateWithLifecycle().value
    val hourOfDay
        @Composable get() = createAttendanceViewModel.hourOfDay.collectAsStateWithLifecycle().value
    val minute
        @Composable get() = createAttendanceViewModel.minute.collectAsStateWithLifecycle().value
    val tickPerSecond: ZonedDateTime
        @Composable get() = createAttendanceViewModel.tickPerSecond.collectAsStateWithLifecycle().value
    val pageIndex
        @Composable get() = createAttendanceViewModel.pageIndex.collectAsStateWithLifecycle().value
    var showCancelConfirmationDialog by mutableStateOf(false)
        private set
    var showCreateAttendanceProgressDialog by mutableStateOf(false)
        private set

    fun onCookieChange(cookie: String) {
        createAttendanceViewModel.setCookie(cookie)
    }

    fun onHourOfDayChange(hourOfDay: Int) {
        createAttendanceViewModel.setHourOfDay(hourOfDay)
    }

    fun onMinuteChange(minute: Int) {
        createAttendanceViewModel.setMinute(minute)
    }

    fun onLoginHoYoLAB() {
        navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
    }

    fun onNavigateUp() {
        navController.navigateUp()
    }

    fun onCreateAttendance() {
        createAttendanceViewModel.createAttendance()
    }

    fun onNavigateToAttendanceDetailScreen(attendanceId: Long) {
        navController.navigate(
            AttendancesDestination.AttendanceDetailScreen().generateRoute(attendanceId)
        ) {
            popUpTo(AttendancesDestination.AttendancesScreen.route)
        }
    }

    fun onCancelCreateAttendance() {
        navController.popBackStack(
            route = AttendancesDestination.AttendancesScreen.route,
            inclusive = false
        )
    }

    fun onNextButtonClick() {
        val nextPage = createAttendanceViewModel.currentPageIndex + 1

        if (nextPage < pages.size) {
            setPageIndex(nextPage)
        } else if (nextPage == pages.size) {
            onCreateAttendance()
        }
    }

    fun setPageIndex(pageIndex: Int) {
        createAttendanceViewModel.setPageIndex(pageIndex)
    }

    fun onShowCancelConfirmationDialogChange(isShowing: Boolean) {
        showCancelConfirmationDialog = isShowing
    }

    fun onShowCreateAttendanceProgressDialogChange(isShowing: Boolean) {
        showCreateAttendanceProgressDialog = isShowing
    }
}

@Composable
fun rememberCreateAttendanceState(
    navController: NavHostController,
    createAttendanceViewModel: CreateAttendanceViewModel,
    pages: ImmutableList<CreateAttendancePage> = CreateAttendancePage.values().toList()
        .toImmutableList()
) = remember(
    navController,
    createAttendanceViewModel,
    pages
) {
    CreateAttendanceState(
        navController = navController,
        createAttendanceViewModel = createAttendanceViewModel,
        pages = pages
    )
}

enum class CreateAttendancePage {
    GetSession, SelectGames, SetTime;
}