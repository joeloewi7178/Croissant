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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.joeloewi.croissant.util.is24HourFormat
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.util.requestReview
import com.joeloewi.croissant.viewmodel.AttendancesViewModel
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalLifecycleComposeApi
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

    AttendancesContent(
        snackbarHostState = snackbarHostState,
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        onCreateAttendanceClick = {
            navController.navigate(AttendancesDestination.CreateAttendanceScreen.route)
        },
        onDeleteAttendance = attendancesViewModel::deleteAttendance,
        onClickAttendance = {
            navController.navigate(
                AttendancesDestination.AttendanceDetailScreen().generateRoute(it.id)
            )
        },
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
private fun AttendancesContent(
    snackbarHostState: SnackbarHostState,
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    onCreateAttendanceClick: () -> Unit,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (Attendance) -> Unit,
) {

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
                            onClickAttendance = onClickAttendance
                        )
                    } else {
                        AttendanceWithGamesItemPlaceholder(
                            modifier = Modifier.animateItemPlacement(),
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AttendanceWithGamesItem(
    modifier: Modifier,
    item: AttendanceWithGames,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (Attendance) -> Unit
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
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
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

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@Composable
internal fun DismissContent(
    elevation: Dp,
    attendanceWithGames: AttendanceWithGames,
    onClickAttendance: (Attendance) -> Unit,
    onClickOneTimeAttend: () -> Unit
) {
    val attendanceWithGamesState by rememberUpdatedState(attendanceWithGames)

    Row(
        modifier = Modifier
            .shadow(elevation = elevation)
            .clickable { onClickAttendance(attendanceWithGamesState.attendance) }
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
                        attendanceWithGamesState.attendance.nickname
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val context = LocalContext.current
                    val is24HourFormat by context.is24HourFormat().collectAsStateWithLifecycle()
                    val time by remember(attendanceWithGamesState.attendance) {
                        derivedStateOf {
                            with(attendanceWithGamesState.attendance) {
                                "${hourOfDay.toString().padStart(2, '0')} : ${
                                    minute.toString().padStart(2, '0')
                                }"
                            }
                        }
                    }
                    val formattedTime by remember(is24HourFormat, time) {
                        derivedStateOf {
                            if (is24HourFormat) {
                                time
                            } else {
                                LocalTime.parse(
                                    time,
                                    DateTimeFormatter.ofPattern(
                                        "HH : mm",
                                    )
                                ).format(DateTimeFormatter.ofPattern("a hh : mm"))
                            }
                        }
                    }

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
                ) {
                    items(
                        items = attendanceWithGamesState.games,
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
                val workInfos by WorkManager.getInstance(LocalContext.current)
                    .getWorkInfosForUniqueWorkLiveData(attendanceWithGamesState.attendance.oneTimeAttendCheckInEventWorkerName.toString())
                    .observeAsState()
                val isRunning by remember(workInfos) {
                    derivedStateOf {
                        workInfos?.any { it.state == WorkInfo.State.RUNNING }
                    }
                }

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