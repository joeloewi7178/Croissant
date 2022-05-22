package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.work.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.util.requestReview
import com.joeloewi.croissant.viewmodel.AttendancesViewModel
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun AttendancesScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    attendancesViewModel: AttendancesViewModel = hiltViewModel()
) {
    val pagedAttendancesWithGames =
        attendancesViewModel.pagedAttendanceWithGames.collectAsLazyPagingItems()
    val notifyMigrateToAlarmManager by attendancesViewModel.notifyMigrateToAlarmManager.collectAsState()

    AttendancesContent(
        snackbarHostState = snackbarHostState,
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        notifyMigrateToAlarmManager = notifyMigrateToAlarmManager,
        onCreateAttendanceClick = {
            navController.navigate(AttendancesDestination.CreateAttendanceScreen.route)
        },
        onDeleteAttendance = attendancesViewModel::deleteAttendance,
        onClickAttendance = {
            navController.navigate(
                AttendancesDestination.AttendanceDetailScreen().generateRoute(it.id)
            )
        },
        onNotifyMigrateToAlarmManagerChange = attendancesViewModel::setNotifyMigrateToAlarmManager
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
private fun AttendancesContent(
    snackbarHostState: SnackbarHostState,
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    notifyMigrateToAlarmManager: Boolean,
    onCreateAttendanceClick: () -> Unit,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (Attendance) -> Unit,
    onNotifyMigrateToAlarmManagerChange: (Boolean) -> Unit
) {
    val (oneTimeAttendanceExecuted, onOneTimeAttendanceExecutedChange) = rememberSaveable {
        mutableStateOf(
            false
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.attendance))
                }
            )
        },
        snackbarHost = {
            androidx.compose.material3.SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateAttendanceClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name
                )
            }
        },
    ) { innerPadding ->
        if (pagedAttendancesWithGames.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .then(Modifier.padding(DoubleDp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(0.3f),
                    imageVector = Icons.Default.Error,
                    contentDescription = Icons.Default.Error.name,
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
                Text(
                    text = stringResource(id = R.string.attendance_is_empty),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.can_attend_event_by_creating_attendance),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(
                    items = pagedAttendancesWithGames,
                    key = { item -> item.attendance.id }
                ) { item ->
                    if (item != null) {
                        AttendanceWithGamesItem(
                            modifier = Modifier.animateItemPlacement(),
                            item = item,
                            onDeleteAttendance = onDeleteAttendance,
                            onClickAttendance = onClickAttendance,
                            onClickOneTimeAttend = {
                                onOneTimeAttendanceExecutedChange(true)
                            }
                        )
                    } else {
                        AttendanceWithGamesItemPlaceholder(
                            modifier = Modifier.animateItemPlacement(),
                        )
                    }
                }
            }
        }

        if (oneTimeAttendanceExecuted && notifyMigrateToAlarmManager) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = {
                    onOneTimeAttendanceExecutedChange(false)
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            onOneTimeAttendanceExecutedChange(false)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = Icons.Default.NewReleases.name
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.note))
                },
                text = {
                    Column {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(id = R.string.notify_migrate_alarm_manager_text)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = DefaultDp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Row(
                                modifier = Modifier.toggleable(
                                    value = notifyMigrateToAlarmManager,
                                    role = Role.Checkbox,
                                    onValueChange = onNotifyMigrateToAlarmManagerChange
                                ),
                                horizontalArrangement = Arrangement.spacedBy(DefaultDp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Checkbox(
                                    checked = notifyMigrateToAlarmManager,
                                    onCheckedChange = null
                                )
                                Text(text = stringResource(id = R.string.do_not_show_again))
                            }
                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AttendanceWithGamesItem(
    modifier: Modifier,
    item: AttendanceWithGames,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (Attendance) -> Unit,
    onClickOneTimeAttend: () -> Unit
) {
    val dismissState = rememberDismissState()
    val isDismissedEndToStart = dismissState.isDismissed(DismissDirection.EndToStart)
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isDismissedEndToStart) {
        if (isDismissedEndToStart) {
            onDeleteAttendance(item.attendance)
        }
    }

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { direction ->
            FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.25f else 0.5f)
        },
        background = {
            SwipeToDismissBackground(dismissState = dismissState)
        },
        dismissContent = {
            DismissContent(
                elevation = animateDpAsState(
                    if (dismissState.dismissDirection != null) HalfDp else 0.dp
                ).value,
                attendanceWithGames = item,
                onClickOneTimeAttend = {
                    val attendance = item.attendance
                    val oneTimeWork = OneTimeWorkRequestBuilder<AttendCheckInEventWorker>()
                        .setInputData(workDataOf(AttendCheckInEventWorker.ATTENDANCE_ID to attendance.id))
                        .setConstraints(
                            Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()
                        )
                        .build()

                    WorkManager.getInstance(context).beginUniqueWork(
                        attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                        ExistingWorkPolicy.APPEND_OR_REPLACE,
                        oneTimeWork
                    ).enqueue()

                    onClickOneTimeAttend()

                    coroutineScope.launch {
                        requestReview(
                            context = context,
                            activity = activity,
                            logMessage = "ImmediateAttendance"
                        )
                    }
                },
                onClickAttendance = onClickAttendance
            )
        }
    )
}

@ExperimentalMaterialApi
@Composable
internal fun SwipeToDismissBackground(
    dismissState: DismissState
) {
    val direction =
        dismissState.dismissDirection ?: return
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.surfaceVariant
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.surface
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
        }
    )
    val iconColor by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> MaterialTheme.colorScheme.onSurfaceVariant
            DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.onSurface
            DismissValue.DismissedToStart -> MaterialTheme.colorScheme.onError
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = DoubleDp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.scale(scale),
            tint = iconColor
        )
    }
}

@ExperimentalFoundationApi
@Composable
internal fun DismissContent(
    elevation: Dp,
    attendanceWithGames: AttendanceWithGames,
    onClickAttendance: (Attendance) -> Unit,
    onClickOneTimeAttend: () -> Unit
) {
    Row(
        modifier = Modifier
            .shadow(elevation = elevation)
            .clickable { onClickAttendance(attendanceWithGames.attendance) }
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(DoubleDp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.attendance_of_nickname,
                        attendanceWithGames.attendance.nickname
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.scheduled_time,
                            attendanceWithGames.attendance.hourOfDay.toString().padStart(2, '0'),
                            attendanceWithGames.attendance.minute.toString().padStart(2, '0')
                        ),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
                ) {
                    items(
                        items = attendanceWithGames.games,
                        key = { it.id }
                    ) { game ->
                        AsyncImage(
                            modifier = Modifier
                                .animateItemPlacement()
                                .size(IconDp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(game.type.gameIconUrl)
                                .build(),
                            contentDescription = null
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val attendance = attendanceWithGames.attendance
                val workInfos by WorkManager.getInstance(LocalContext.current)
                    .getWorkInfosForUniqueWorkLiveData(attendance.oneTimeAttendCheckInEventWorkerName.toString())
                    .observeAsState()
                val isRunning = workInfos?.any { it.state == WorkInfo.State.RUNNING }

                IconButton(
                    enabled = isRunning == false,
                    onClick = onClickOneTimeAttend
                ) {
                    AnimatedVisibility(
                        visible = isRunning == false,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = Icons.Default.PlayCircle.name
                        )
                    }

                    AnimatedVisibility(
                        visible = isRunning == true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = Icons.Default.Pending.name
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun AttendanceWithGamesItemPlaceholder(
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                text = "",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = DefaultDp,
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.background,
                            )
                        ),
                    text = "",
                    style = MaterialTheme.typography.headlineMedium
                )

                AsyncImage(
                    modifier = Modifier
                        .size(IconDp)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.background,
                            )
                        ),
                    model = ImageRequest.Builder(
                        LocalContext.current
                    ).build(),
                    contentDescription = null
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
            ) {
                items(
                    items = IntArray(3) { it }.toTypedArray(),
                    key = { "placeholder${it}" }
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .animateItemPlacement()
                            .size(IconDp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .build(),
                        contentDescription = null
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(IconDp)
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
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
fun WorkInfoStateIndicator(
    workInfo: WorkInfo?
) {
    if (workInfo != null) {
        when (workInfo.state) {

            //periodic work has only two states

            WorkInfo.State.ENQUEUED -> {
                Icon(
                    imageVector = Icons.Default.PendingActions,
                    contentDescription = Icons.Default.PendingActions.name
                )
            }

            WorkInfo.State.RUNNING -> {
                Icon(
                    imageVector = Icons.Default.Pending,
                    contentDescription = Icons.Default.Pending.name
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = Icons.Default.QuestionMark.name
                )
            }
        }
    } else {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = Icons.Default.Error.name
        )
    }
}