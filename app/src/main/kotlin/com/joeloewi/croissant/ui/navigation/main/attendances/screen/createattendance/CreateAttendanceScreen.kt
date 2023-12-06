package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.entity.Attendance
import com.joeloewi.croissant.domain.entity.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.GetSession
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.SelectGames
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable.SetTime
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

@Composable
fun CreateAttendanceScreen(
    createAttendanceViewModel: CreateAttendanceViewModel = hiltViewModel(),
    newCookie: () -> String,
    onLoginHoYoLAB: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val insertAttendanceState by createAttendanceViewModel.insertAttendanceState.collectAsStateWithLifecycle(
        context = Dispatchers.Default
    )
    val connectedGames by createAttendanceViewModel.connectedGames.collectAsStateWithLifecycle(
        context = Dispatchers.Default
    )
    val duplicateAttendance by createAttendanceViewModel.duplicatedAttendance.collectAsStateWithLifecycle(
        context = Dispatchers.Default
    )
    val hourOfDay by createAttendanceViewModel.hourOfDay.collectAsStateWithLifecycle(
        context = Dispatchers.Default
    )
    val minute by createAttendanceViewModel.minute.collectAsStateWithLifecycle(context = Dispatchers.Default)
    val tickPerSecond by createAttendanceViewModel.tickPerSecond.collectAsStateWithLifecycle(context = Dispatchers.Default)

    CreateAttendanceContent(
        newCookie = newCookie,
        insertAttendanceState = { insertAttendanceState },
        connectedGames = { connectedGames },
        duplicateAttendance = { duplicateAttendance },
        hourOfDay = { hourOfDay },
        minute = { minute },
        tickPerSecond = { tickPerSecond },
        onLoginHoYoLAB = onLoginHoYoLAB,
        onHourOfDayChange = createAttendanceViewModel::setHourOfDay,
        onMinuteChange = createAttendanceViewModel::setMinute,
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateAttendanceContent(
    newCookie: () -> String,
    insertAttendanceState: () -> Lce<List<Long>>,
    connectedGames: () -> Lce<List<GameRecord>>,
    duplicateAttendance: () -> Attendance?,
    hourOfDay: () -> Int,
    minute: () -> Int,
    tickPerSecond: () -> ZonedDateTime,
    onLoginHoYoLAB: () -> Unit,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onNavigateUp: () -> Unit
) {
    val pagerState = rememberPagerState { 3 }
    val coroutineScope = rememberCoroutineScope()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    var showCancelConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            snapshotFlow(newCookie).catch { }.collect {
                pagerState.scrollToPage(1)
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            snapshotFlow(insertAttendanceState).catch { }.collect {
                when (it) {
                    is Lce.Content -> {
                        withContext(Dispatchers.Main) {
                            if (it.content.isEmpty()) {
                                onNavigateUp()
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

    BackHandler(
        enabled = pagerState.currentPage <= 1
    ) {
        showCancelConfirmationDialog = true
    }

    BackHandler(
        enabled = pagerState.currentPage > 1
    ) {
        coroutineScope.launch {
            pagerState.scrollToPage(pagerState.currentPage - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.create_attendance))
                },
                navigationIcon = viewModelStoreOwner.navigationIconButton(
                    onClick = {
                        showCancelConfirmationDialog = true
                    }
                )
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->

        HorizontalPager(
            modifier = Modifier.padding(innerPadding),
            state = pagerState
        ) { page ->
            when (page) {
                0 -> {
                    GetSession(
                        onLoginHoYoLAB = onLoginHoYoLAB
                    )
                }

                1 -> {
                    SelectGames(
                        connectedGames = connectedGames,
                        duplicatedAttendance = duplicateAttendance,
                        onNextButtonClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(page + 1)
                            }
                        },
                        onNavigateToAttendanceDetailScreen = {

                        },
                        onCancelCreateAttendance = {

                        }
                    )
                }

                2 -> {
                    SetTime(
                        hourOfDay = hourOfDay,
                        minute = minute,
                        onHourOfDayChange = onHourOfDayChange,
                        onMinuteChange = onMinuteChange,
                        tickPerSecond = tickPerSecond,
                        onNextButtonClick = {

                        }
                    )
                }
            }
        }

        if (showCancelConfirmationDialog) {
            AlertDialog(
                onDismissRequest = {
                    showCancelConfirmationDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCancelConfirmationDialog = false
                            onNavigateUp()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showCancelConfirmationDialog = false
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

        if (insertAttendanceState().isLoading) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }
    }
}