package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlinx.coroutines.launch

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceDetailScreen(
    navController: NavController,
    attendanceDetailViewModel: AttendanceDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()
    val hourOfDay by attendanceDetailViewModel.hourOfDay.collectAsStateWithLifecycle()
    val minute by attendanceDetailViewModel.minute.collectAsStateWithLifecycle()
    val nickname by attendanceDetailViewModel.nickname.collectAsStateWithLifecycle()
    val uid by attendanceDetailViewModel.uid.collectAsStateWithLifecycle()
    val checkedGame = remember { attendanceDetailViewModel.checkedGames }
    val checkSessionWorkerSuccessLogCount by attendanceDetailViewModel.checkSessionWorkerSuccessLogCount.collectAsStateWithLifecycle()
    val checkSessionWorkerFailureLogCount by attendanceDetailViewModel.checkSessionWorkerFailureLogCount.collectAsStateWithLifecycle()
    val attendCheckInEventWorkerSuccessLogCount by attendanceDetailViewModel.attendCheckInEventWorkerSuccessLogCount.collectAsStateWithLifecycle()
    val attendCheckInEventWorkerFailureLogCount by attendanceDetailViewModel.attendCheckInEventWorkerFailureLogCount.collectAsStateWithLifecycle()
    val updateAttendanceState by attendanceDetailViewModel.updateAttendanceState.collectAsStateWithLifecycle()
    val attendanceWithGamesState by attendanceDetailViewModel.attendanceWithGamesState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val hasExecutedAtLeastOnce by remember(
        attendCheckInEventWorkerSuccessLogCount,
        attendCheckInEventWorkerFailureLogCount
    ) {
        derivedStateOf { attendCheckInEventWorkerSuccessLogCount > 0 || attendCheckInEventWorkerFailureLogCount > 0 }
    }

    LaunchedEffect(attendanceDetailViewModel) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            attendanceDetailViewModel.setCookie(cookie = it)
            snackbarHostState.showSnackbar(context.getString(R.string.press_save_button_to_commit))
        }
    }

    LaunchedEffect(hasExecutedAtLeastOnce) {
        if (hasExecutedAtLeastOnce) {
            requestReview(
                context = context,
                activity = activity,
                logMessage = "ExecutedAtLeastOnce"
            )
        }
    }

    AttendanceDetailContent(
        snackbarHostState = snackbarHostState,
        attendanceWithGamesState = attendanceWithGamesState,
        previousBackStackEntry = navController.previousBackStackEntry,
        hourOfDay = hourOfDay,
        minute = minute,
        nickname = nickname,
        uid = uid,
        checkedGames = checkedGame,
        checkSessionWorkerSuccessLogCount = checkSessionWorkerSuccessLogCount,
        checkSessionWorkerFailureLogCount = checkSessionWorkerFailureLogCount,
        attendCheckInEventWorkerSuccessLogCount = attendCheckInEventWorkerSuccessLogCount,
        attendCheckInEventWorkerFailureLogCount = attendCheckInEventWorkerFailureLogCount,
        updateAttendanceState = updateAttendanceState,
        onHourOfDayChange = attendanceDetailViewModel::setHourOfDay,
        onMinuteChange = attendanceDetailViewModel::setMinute,
        onNavigateUp = {
            navController.navigateUp()
        },
        onClickLogSummary = {
            navController.navigate(
                AttendancesDestination.AttendanceLogsScreen().generateRoute(
                    attendanceDetailViewModel.attendanceId,
                    it
                )
            )
        },
        onClickRefreshSession = {
            navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
        },
        onClickSave = {
            if (checkedGame.isEmpty()) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.select_at_least_one_game))
                }
            } else {
                attendanceDetailViewModel.updateAttendance()
            }
        }
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun AttendanceDetailContent(
    snackbarHostState: SnackbarHostState,
    attendanceWithGamesState: Lce<AttendanceWithGames>,
    previousBackStackEntry: NavBackStackEntry?,
    hourOfDay: Int,
    minute: Int,
    nickname: String,
    uid: Long,
    checkedGames: SnapshotStateList<Game>,
    checkSessionWorkerSuccessLogCount: Long,
    checkSessionWorkerFailureLogCount: Long,
    attendCheckInEventWorkerSuccessLogCount: Long,
    attendCheckInEventWorkerFailureLogCount: Long,
    updateAttendanceState: Lce<Unit?>,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onNavigateUp: () -> Unit,
    onClickLogSummary: (LoggableWorker) -> Unit,
    onClickRefreshSession: () -> Unit,
    onClickSave: () -> Unit
) {
    val scrollableState = rememberScrollState()
    val (showUpdateAttendanceProgressDialog, onShowUpdateAttendanceProgressDialogChange) = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(updateAttendanceState) {
        when (updateAttendanceState) {
            is Lce.Loading -> {
                onShowUpdateAttendanceProgressDialogChange(true)
            }

            is Lce.Content -> {
                onShowUpdateAttendanceProgressDialogChange(false)
                if (updateAttendanceState.content != null) {
                    onNavigateUp()
                }
            }

            is Lce.Error -> {
                onShowUpdateAttendanceProgressDialogChange(false)
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.attendance_of_nickname, nickname))
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = previousBackStackEntry,
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = onClickSave,
                        enabled = attendanceWithGamesState is Lce.Content
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = Icons.Default.Save.name
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        val loggableWorkers =
            remember { LoggableWorker.values().filter { it != LoggableWorker.UNKNOWN } }

        Column(
            modifier = Modifier
                .verticalScroll(scrollableState)
                .fillMaxSize()
                .padding(innerPadding)
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                text = stringResource(id = R.string.session),
                style = MaterialTheme.typography.headlineSmall
            )

            Column(
                modifier = Modifier
                    .padding(DefaultDp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(
                    space = DefaultDp,
                    alignment = Alignment.CenterVertically
                )
            ) {
                listOf(
                    stringResource(id = R.string.uid) to uid.toString(),
                    stringResource(id = R.string.nickname) to nickname,
                    stringResource(id = R.string.games_to_attend) to ""
                ).forEach {
                    SessionInfoRow(
                        key = it.first,
                        value = it.second
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
                ) {
                    items(
                        items = HoYoLABGame.values().filter { it != HoYoLABGame.Unknown },
                        key = { it.name }
                    ) { item ->
                        ConnectedGameListItem(
                            modifier = Modifier.animateItemPlacement(),
                            hoYoLABGame = item,
                            checkedGames = checkedGames,
                        )
                    }
                }

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickRefreshSession
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = Icons.Default.Refresh.name
                        )
                        Text(text = stringResource(id = R.string.refresh_session))
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.schedule_time_setting),
                style = MaterialTheme.typography.headlineSmall
            )

            TimePicker(
                modifier = Modifier.fillMaxWidth(),
                hourOfDay = hourOfDay,
                minute = minute,
                onHourOfDayChange = onHourOfDayChange,
                onMinuteChange = onMinuteChange
            )

            Text(
                text = stringResource(id = R.string.execution_log_summary),
                style = MaterialTheme.typography.headlineSmall
            )

            loggableWorkers.forEach {
                when (it) {
                    LoggableWorker.ATTEND_CHECK_IN_EVENT -> {
                        LogSummaryRow(
                            title = stringResource(id = R.string.attendance),
                            failureLogCount = attendCheckInEventWorkerFailureLogCount,
                            successLogCount = attendCheckInEventWorkerSuccessLogCount,
                            onClickLogSummary = {
                                onClickLogSummary(it)
                            }
                        )
                    }
                    LoggableWorker.CHECK_SESSION -> {
                        LogSummaryRow(
                            title = stringResource(id = R.string.session_validation),
                            failureLogCount = checkSessionWorkerFailureLogCount,
                            successLogCount = checkSessionWorkerSuccessLogCount,
                            onClickLogSummary = {
                                onClickLogSummary(it)
                            }
                        )
                    }
                    LoggableWorker.UNKNOWN -> {
                        LogSummaryRow(
                            title = "",
                            failureLogCount = 0,
                            successLogCount = 0,
                            onClickLogSummary = {}
                        )
                    }
                }
            }
        }

        if (showUpdateAttendanceProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    onShowUpdateAttendanceProgressDialogChange(false)
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGameListItem(
    modifier: Modifier,
    hoYoLABGame: HoYoLABGame,
    checkedGames: SnapshotStateList<Game>
) {
    val game by rememberUpdatedState(
        Game(
            type = hoYoLABGame,
        )
    )

    Card(
        onClick = {
            val checked = checkedGames.contains(game)

            if (!checked) {
                checkedGames.add(game)
            } else {
                checkedGames.remove(game)
            }
        },
        modifier = modifier.size(120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(DefaultDp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(3f),
                    text = stringResource(id = hoYoLABGame.gameNameStringResId()),
                    style = MaterialTheme.typography.labelMedium
                )

                Checkbox(
                    modifier = Modifier.weight(1f),
                    checked = checkedGames.contains(game),
                    onCheckedChange = null
                )
            }

            AsyncImage(
                modifier = Modifier
                    .size(IconDp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(hoYoLABGame.gameIconUrl)
                    .build(),
                contentDescription = null
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGameListItemPlaceHolder(
    modifier: Modifier,
) {
    Card(
        modifier = modifier.size(120.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(DefaultDp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .width(64.dp)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    text = ""
                )

                AsyncImage(
                    modifier = Modifier
                        .size(IconDp)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).build(),
                    contentDescription = null
                )
            }

            AsyncImage(
                modifier = Modifier
                    .size(IconDp)
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                model = ImageRequest.Builder(
                    LocalContext.current
                ).build(),
                contentDescription = null
            )
        }
    }
}

@Composable
fun LogSummaryRow(
    title: String,
    failureLogCount: Long,
    successLogCount: Long,
    onClickLogSummary: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable {
                onClickLogSummary()
            }
            .padding(DefaultDp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DefaultDp,
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DefaultDp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = Icons.Default.Error.name,
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "$failureLogCount",
                    color = MaterialTheme.colorScheme.error
                )

                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = Icons.Default.Done.name
                )

                Text(text = "$successLogCount")
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = Icons.Default.NavigateNext.name
            )
        }
    }
}

@Composable
fun SessionInfoRow(
    key: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            DefaultDp
        )
    ) {
        Text(
            text = key,
            style = TextStyle(
                fontWeight = FontWeight.Bold
            )
        )
        Text(text = value)
    }
}