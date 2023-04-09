package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.state.AttendanceDetailState
import com.joeloewi.croissant.state.rememberAttendanceDetailState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel

@Composable
fun AttendanceDetailScreen(
    navController: NavHostController,
    attendanceDetailViewModel: AttendanceDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val attendanceDetailState =
        rememberAttendanceDetailState(
            snackbarHostState = remember { SnackbarHostState() },
            attendanceDetailViewModel = attendanceDetailViewModel,
            navController = navController
        )
    val isNavigateUpRequested = attendanceDetailState.isNavigateUpRequested
    val hasExecutedAtLeastOnce = attendanceDetailState.hasExecutedAtLeastOnce

    LaunchedEffect(attendanceDetailViewModel) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            attendanceDetailViewModel.setCookie(cookie = it)
            attendanceDetailState.snackbarHostState.showSnackbar(context.getString(R.string.press_save_button_to_commit))
        }
    }

    LaunchedEffect(isNavigateUpRequested) {
        if (isNavigateUpRequested) {
            navController.navigateUp()
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
        attendanceDetailState = attendanceDetailState
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun AttendanceDetailContent(
    attendanceDetailState: AttendanceDetailState,
) {
    val list by rememberUpdatedState(
        newValue = listOf(
            stringResource(id = R.string.uid) to attendanceDetailState.uid.toString(),
            stringResource(id = R.string.nickname) to attendanceDetailState.nickname,
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            id = R.string.attendance_of_nickname,
                            attendanceDetailState.nickname
                        )
                    )
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = attendanceDetailState.previousBackStackEntry,
                    onClick = attendanceDetailState::onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = attendanceDetailState::onClickSave,
                        enabled = attendanceDetailState.isSuccessfullyLoaded
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
            SnackbarHost(hostState = attendanceDetailState.snackbarHostState)
        },
        contentWindowInsets = WindowInsets.displayCutout
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(Modifier.padding(horizontal = DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
                alignment = Alignment.CenterVertically
            )
        ) {
            item("userInformationHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.session),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("sessionInfos") {
                list.forEach {
                    SessionInfoRow(
                        key = it.first,
                        value = it.second
                    )
                }
            }

            item("changeCookie") {
                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = attendanceDetailState::onClickRefreshSession
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
                        Text(
                            text = stringResource(id = R.string.refresh_session),
                        )
                    }
                }
            }

            item("gamesHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.games_to_attend),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("games") {
                Column {
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
                                checkedGames = attendanceDetailState.checkedGames,
                            )
                        }
                    }
                }
            }

            item("timeSettingHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.schedule_time_setting),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("timePicker") {
                TimePicker(
                    modifier = Modifier.fillMaxWidth(),
                    hourOfDay = attendanceDetailState.hourOfDay,
                    minute = attendanceDetailState.minute,
                    onHourOfDayChange = attendanceDetailState::onHourOfDayChange,
                    onMinuteChange = attendanceDetailState::onMinuteChange
                )
            }

            item("logSummaryHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.execution_log_summary),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item(LoggableWorker.ATTEND_CHECK_IN_EVENT) {
                LogSummaryRow(
                    title = stringResource(id = R.string.attendance),
                    failureLogCount = attendanceDetailState.attendCheckInEventWorkerFailureLogCount,
                    successLogCount = attendanceDetailState.attendCheckInEventWorkerSuccessLogCount,
                    onClickLogSummary = {
                        attendanceDetailState.onClickLogSummary(LoggableWorker.ATTEND_CHECK_IN_EVENT)
                    }
                )
            }

            item(LoggableWorker.CHECK_SESSION) {
                LogSummaryRow(
                    title = stringResource(id = R.string.session_validation),
                    failureLogCount = attendanceDetailState.checkSessionWorkerFailureLogCount,
                    successLogCount = attendanceDetailState.checkSessionWorkerSuccessLogCount,
                    onClickLogSummary = {
                        attendanceDetailState.onClickLogSummary(LoggableWorker.CHECK_SESSION)
                    }
                )
            }
        }

        if (attendanceDetailState.isProgressDialogShowing) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    .size(IconDp)
                    .clip(MaterialTheme.shapes.extraSmall),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(hoYoLABGame.gameIconUrl)
                    .build(),
                contentDescription = null
            )
        }
    }
}

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
                            shape = MaterialTheme.shapes.extraSmall,
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
                            shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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