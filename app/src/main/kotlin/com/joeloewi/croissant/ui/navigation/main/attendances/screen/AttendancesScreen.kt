package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.core.data.model.relational.AttendanceWithGames
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.dateTimeFormatterPerHourFormat
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.util.requestReview
import com.joeloewi.croissant.viewmodel.AttendancesViewModel
import com.joeloewi.croissant.worker.AttendCheckInEventWorker
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.foundation.fade
import io.github.fornewid.placeholder.foundation.placeholder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun AttendancesScreen(
    snackbarHostState: SnackbarHostState,
    attendancesViewModel: AttendancesViewModel = hiltViewModel(),
    onClickCreateAttendance: () -> Unit,
    onClickAttendance: (attendanceId: Long) -> Unit
) {
    val pagedAttendancesWithGames =
        attendancesViewModel.pagedAttendanceWithGames.collectAsLazyPagingItems()

    attendancesViewModel.collectSideEffect {
        when (it) {
            is AttendancesViewModel.SideEffect.OnClickAttendance -> onClickAttendance(it.attendanceId)
            AttendancesViewModel.SideEffect.OnClickCreateAttendance -> onClickCreateAttendance()
        }
    }

    AttendancesContent(
        snackbarHostState = snackbarHostState,
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        onClickCreateAttendance = attendancesViewModel::onClickCreateAttendance,
        onDeleteAttendance = attendancesViewModel::deleteAttendance,
        onClickAttendance = attendancesViewModel::onClickAttendance
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun AttendancesContent(
    snackbarHostState: SnackbarHostState,
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    onClickCreateAttendance: () -> Unit,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (attendanceId: Long) -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.attendance))
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickCreateAttendance
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (pagedAttendancesWithGames.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize()
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
                }
            } else {
                items(
                    count = pagedAttendancesWithGames.itemCount,
                    key = pagedAttendancesWithGames.itemKey { it.attendance.id }
                ) { index ->
                    val item = runCatching { pagedAttendancesWithGames[index] }.getOrNull()

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceWithGamesItem(
    modifier: Modifier,
    item: AttendanceWithGames,
    onDeleteAttendance: (Attendance) -> Unit,
    onClickAttendance: (attendanceId: Long) -> Unit
) {
    val swipeToDismissState = rememberSwipeToDismissBoxState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snapshotFlow { swipeToDismissState.currentValue }.catch { }.collectLatest {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDeleteAttendance(item.attendance)
            }
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeToDismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            SwipeToDismissBackground(
                targetValue = swipeToDismissState.targetValue
            )
        }
    ) {
        DismissContent(
            dismissDirection = swipeToDismissState.dismissDirection,
            attendanceWithGames = item,
            onClickOneTimeAttend = remember {
                { attendance ->
                    Firebase.analytics.logEvent("instant_attend_click", bundleOf())

                    val oneTimeWork = AttendCheckInEventWorker.buildOneTimeWork(
                        attendanceId = attendance.id,
                        isInstantAttendance = true
                    )

                    WorkManager.getInstance(context).beginUniqueWork(
                        attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                        ExistingWorkPolicy.REPLACE,
                        oneTimeWork
                    ).enqueue()

                    coroutineScope.launch {
                        withContext(Dispatchers.Default) {
                            requestReview(
                                activity = activity,
                                logMessage = "ImmediateAttendance"
                            )
                        }
                    }
                }
            },
            onClickAttendance = onClickAttendance
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissBackground(
    targetValue: SwipeToDismissBoxValue
) {
    val scale by animateFloatAsState(
        if (targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f, label = ""
    )
    val backgroundColor by animateColorAsState(
        when (targetValue) {
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
            else -> {
                MaterialTheme.colorScheme.surfaceColorAtElevation(HalfDp)
            }
        }, label = ""
    )
    val iconColor by animateColorAsState(
        when (targetValue) {
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
            else -> {
                MaterialTheme.colorScheme.onSurface
            }
        }, label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = DoubleDp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier.scale(scale),
            tint = iconColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DismissContent(
    dismissDirection: SwipeToDismissBoxValue,
    attendanceWithGames: AttendanceWithGames,
    onClickAttendance: (attendanceId: Long) -> Unit,
    onClickOneTimeAttend: (Attendance) -> Unit
) {
    ListItem(
        modifier = Modifier
            .composed {
                val elevation by animateDpAsState(
                    if (dismissDirection != SwipeToDismissBoxValue.Settled) HalfDp else 0.dp,
                    label = ""
                )

                remember(elevation, attendanceWithGames.attendance.id) {
                    shadow(elevation).clickable { onClickAttendance(attendanceWithGames.attendance.id) }
                }
            },
        supportingContent = {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(space = HalfDp),
                userScrollEnabled = false
            ) {
                items(
                    items = attendanceWithGames.games,
                    key = { it.id }
                ) { game ->
                    AsyncImage(
                        modifier = Modifier
                            .animateItemPlacement()
                            .size(IconDp)
                            .shadow(4.dp, shape = MaterialTheme.shapes.extraSmall),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(game.type.gameIconUrl)
                            .build(),
                        contentDescription = null
                    )
                }
            }
        },
        headlineContent = {
            val hourFormat = LocalHourFormat.current

            val formattedTime by remember(
                attendanceWithGames.attendance,
                hourFormat
            ) {
                derivedStateOf {
                    with(attendanceWithGames.attendance) {
                        ZonedDateTime.now(ZoneId.of(timezoneId))
                            .withHour(hourOfDay)
                            .withMinute(minute)
                            .withSecond(30)
                            .withNano(0)
                    }.format(
                        dateTimeFormatterPerHourFormat(hourFormat)
                    )
                }
            }

            Text(
                text = buildAnnotatedString {
                    append(
                        AnnotatedString(
                            formattedTime,
                            spanStyle = MaterialTheme.typography.headlineSmall.toSpanStyle()
                        )
                    )
                    append(" ")
                    append("(${attendanceWithGames.attendance.timezoneId})")
                }
            )
        },
        overlineContent = {
            Text(
                text = stringResource(
                    id = R.string.attendance_of_nickname,
                    attendanceWithGames.attendance.nickname
                ),
                style = MaterialTheme.typography.titleMedium
            )
        },
        trailingContent = {
            val context = LocalContext.current
            val lifecycleOwner = LocalLifecycleOwner.current
            val workerName =
                attendanceWithGames.attendance.oneTimeAttendCheckInEventWorkerName.toString()
            val isRunningFlow = remember(context, workerName) {
                WorkManager.getInstance(context)
                    .getWorkInfosFlow(
                        WorkQuery.Builder
                            .fromUniqueWorkNames(listOf(workerName))
                            .addStates(listOf(WorkInfo.State.RUNNING))
                            .build()
                    )
                    .catch { }
                    .map { it.isNotEmpty() }
                    .flowOn(Dispatchers.IO)
            }
            val isRunning by produceState(initialValue = false) {
                isRunningFlow.flowWithLifecycle(lifecycleOwner.lifecycle).collect { value = it }
            }

            IconButton(
                enabled = !isRunning,
                onClick = remember {
                    { onClickOneTimeAttend(attendanceWithGames.attendance) }
                }
            ) {
                AnimatedContent(
                    targetState = isRunning,
                    label = ""
                ) { state ->
                    if (state) {
                        Icon(
                            imageVector = Icons.Default.Pending,
                            contentDescription = Icons.Default.Pending.name
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = Icons.Default.PlayCircle.name
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AttendanceWithGamesItemPlaceholder(
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
                        shape = MaterialTheme.shapes.extraSmall,
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
                            shape = MaterialTheme.shapes.extraSmall,
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
                            shape = MaterialTheme.shapes.extraSmall,
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
                            .clip(MaterialTheme.shapes.extraSmall)
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
                        shape = MaterialTheme.shapes.extraSmall,
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