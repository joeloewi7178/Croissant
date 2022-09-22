package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.CreateAttendancePage
import com.joeloewi.croissant.state.CreateAttendanceState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberCreateAttendanceState
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.COOKIE
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.GetSession
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.SelectGames
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.SetTime
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.getResultFromPreviousComposable
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceScreen(
    navController: NavController,
    createAttendanceViewModel: CreateAttendanceViewModel
) {
    val createAttendanceState = rememberCreateAttendanceState(
        navController = navController,
        createAttendanceViewModel = createAttendanceViewModel
    )

    LaunchedEffect(createAttendanceState) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            createAttendanceState.onCookieChange(cookie = it)
        }
    }

    CreateAttendanceContent(
        createAttendanceState = createAttendanceState,
    )
}

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceContent(
    createAttendanceState: CreateAttendanceState
) {
    val insertAttendanceState = createAttendanceState.insertAttendanceState
    val cookie = createAttendanceState.cookie
    val pageIndex = createAttendanceState.pageIndex

    LaunchedEffect(cookie) {
        if (cookie.isNotEmpty() && pageIndex == 0) {
            createAttendanceState.setPageIndex(1)
        }
    }

    LaunchedEffect(insertAttendanceState) {
        with(createAttendanceState) {
            when (insertAttendanceState) {
                is Lce.Loading -> {
                    onShowCreateAttendanceProgressDialogChange(true)
                }

                is Lce.Content -> {
                    onShowCreateAttendanceProgressDialogChange(false)
                    if (insertAttendanceState.content.isNotEmpty()) {
                        onNavigateUp()
                    }
                }

                is Lce.Error -> {
                    onShowCreateAttendanceProgressDialogChange(false)
                }
            }
        }
    }

    BackHandler(
        enabled = pageIndex <= 1
    ) {
        createAttendanceState.onShowCancelConfirmationDialogChange(true)
    }

    BackHandler(
        enabled = pageIndex > 1
    ) {
        createAttendanceState.setPageIndex(pageIndex - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.create_attendance))
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = createAttendanceState.previousBackStackEntry,
                    onClick = {
                        createAttendanceState.onShowCancelConfirmationDialogChange(true)
                    }
                )
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.ime)
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(DefaultDp)
        ) {
            when (createAttendanceState.pages[pageIndex]) {
                CreateAttendancePage.GetSession -> {
                    GetSession(
                        modifier = Modifier.padding(innerPadding),
                        onLoginHoYoLAB = createAttendanceState::onLoginHoYoLAB
                    )
                }
                CreateAttendancePage.SelectGames -> {
                    SelectGames(
                        modifier = Modifier.padding(innerPadding),
                        createAttendanceState = createAttendanceState
                    )
                }
                CreateAttendancePage.SetTime -> {
                    SetTime(
                        modifier = Modifier.padding(innerPadding),
                        hourOfDay = createAttendanceState.hourOfDay,
                        minute = createAttendanceState.minute,
                        tickPerSecond = { createAttendanceState.tickPerSecond },
                        onNextButtonClick = {
                            createAttendanceState.onNextButtonClick()
                        },
                        onHourOfDayChange = createAttendanceState::onHourOfDayChange,
                        onMinuteChange = createAttendanceState::onMinuteChange
                    )
                }
            }
        }

        if (createAttendanceState.showCancelConfirmationDialog) {
            AlertDialog(
                onDismissRequest = {
                    createAttendanceState.onShowCancelConfirmationDialogChange(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            createAttendanceState.onShowCancelConfirmationDialogChange(false)
                            createAttendanceState.onNavigateUp()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            createAttendanceState.onShowCancelConfirmationDialogChange(false)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.dismiss))
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.caution))
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.unsaved_contents_will_be_disappeared),
                        textAlign = TextAlign.Center
                    )
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }

        if (createAttendanceState.showCreateAttendanceProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    createAttendanceState.onShowCreateAttendanceProgressDialogChange(false)
                }
            )
        }
    }
}