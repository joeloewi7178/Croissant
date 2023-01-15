package com.joeloewi.croissant.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class CreateResinStatusWidgetState(
    private val navController: NavHostController,
    val snackbarHostState: SnackbarHostState,
    private val createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val selectableIntervals = createResinStatusWidgetViewModel.selectableIntervals.toImmutableList()
    val interval
        @Composable get() = createResinStatusWidgetViewModel.interval.collectAsStateWithLifecycle().value
    val userInfos = createResinStatusWidgetViewModel.userInfos
    val insertResinStatusWidgetState
        @Composable get() = createResinStatusWidgetViewModel.createResinStatusWidgetState.collectAsStateWithLifecycle().value
    val getUserInfoState
        @Composable get() = createResinStatusWidgetViewModel.getUserInfoState.collectAsStateWithLifecycle().value
    val appWidgetId
        get() = createResinStatusWidgetViewModel.appWidgetId
    val showProgressDialog
        @Composable get() = insertResinStatusWidgetState.isLoading
    val showUserInfoProgressDialog
        @Composable get() = getUserInfoState.isLoading

    fun onClickDone() {
        createResinStatusWidgetViewModel.configureAppWidget()
    }

    fun onIntervalChange(interval: Long) {
        createResinStatusWidgetViewModel.setInterval(interval)
    }

    fun onReceiveCookie(cookie: String) {
        createResinStatusWidgetViewModel.onReceiveCookie(cookie)
    }

    fun onClickAdd() {
        navController.navigate(ResinStatusWidgetConfigurationDestination.LoginHoYoLABScreen.route)
    }
}

@Composable
fun rememberCreateResinStatusWidgetState(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) = remember(
    navController,
    snackbarHostState,
    createResinStatusWidgetViewModel
) {
    CreateResinStatusWidgetState(
        navController = navController,
        snackbarHostState = snackbarHostState,
        createResinStatusWidgetViewModel = createResinStatusWidgetViewModel
    )
}