package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.os.Build
import android.widget.TimePicker
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.util.getResultFromPreviousComposable
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.common.LoggableWorker
import com.joeloewi.domain.entity.Game

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceDetailScreen(
    navController: NavController,
    attendanceDetailViewModel: AttendanceDetailViewModel = hiltViewModel()
) {
    val hourOfDay by attendanceDetailViewModel.hourOfDay.collectAsState()
    val minute by attendanceDetailViewModel.minute.collectAsState()
    val nickname by attendanceDetailViewModel.nickname.collectAsState()
    val uid by attendanceDetailViewModel.uid.collectAsState()
    val checkedGame = attendanceDetailViewModel.checkedGames
    val checkSessionWorkerSuccessLogCount by attendanceDetailViewModel.checkSessionWorkerSuccessLogCount.collectAsState()
    val checkSessionWorkerFailureLogCount by attendanceDetailViewModel.checkSessionWorkerFailureLogCount.collectAsState()
    val attendCheckInEventWorkerSuccessLogCount by attendanceDetailViewModel.attendCheckInEventWorkerSuccessLogCount.collectAsState()
    val attendCheckInEventWorkerFailureLogCount by attendanceDetailViewModel.attendCheckInEventWorkerFailureLogCount.collectAsState()
    val updateAttendanceState by attendanceDetailViewModel.updateAttendanceState.collectAsState()

    LaunchedEffect(attendanceDetailViewModel) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            attendanceDetailViewModel.setCookie(cookie = it)
        }
    }

    AttendanceDetailContent(
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
        onClickSave = attendanceDetailViewModel::updateAttendance
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun AttendanceDetailContent(
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
    val (timePicker, onTimePickerChange) = remember {
        mutableStateOf<TimePicker?>(
            null
        )
    }
    val (showUpdateAttendanceProgressDialog, onShowUpdateAttendanceProgressDialogChange) = rememberSaveable {
        mutableStateOf(false)
    }

    DisposableEffect(timePicker) {
        timePicker?.setOnTimeChangedListener { _, hourOfDay, minute ->
            onHourOfDayChange(hourOfDay)
            onMinuteChange(minute)
        }

        onDispose {
            timePicker?.setOnTimeChangedListener(null)
        }
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
                    Text(text = "${nickname}의 출석 작업")
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = previousBackStackEntry,
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = onClickSave
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = Icons.Default.Save.name
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollableState)
                .padding(innerPadding)
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Text(
                text = "접속 정보",
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
                    "UID" to uid.toString(),
                    "닉네임" to nickname,
                    "출석 작업 설정된 게임" to ""
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
                        Text(text = "접속 정보 갱신하기")
                    }
                }
            }

            Text(
                text = "시간 설정",
                style = MaterialTheme.typography.headlineSmall
            )

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { androidViewContext ->
                    TimePicker(androidViewContext).apply {
                        setIs24HourView(true)
                    }.also(onTimePickerChange)
                }
            ) { view ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hourOfDay.takeIf { it != view.hour }?.let(view::setHour)
                    minute.takeIf { it != view.minute }?.let(view::setMinute)
                } else {
                    hourOfDay.takeIf { it != view.currentHour }?.let(view::setCurrentHour)
                    minute.takeIf { it != view.currentMinute }?.let(view::setCurrentMinute)
                }
            }

            Text(
                text = "실행 기록 요약",
                style = MaterialTheme.typography.headlineSmall
            )

            LoggableWorker.values().filter { it != LoggableWorker.UNKNOWN }.forEach {
                when (it) {
                    LoggableWorker.ATTEND_CHECK_IN_EVENT -> {
                        LogSummaryRow(
                            title = "출석 작업",
                            failureLogCount = attendCheckInEventWorkerFailureLogCount,
                            successLogCount = attendCheckInEventWorkerSuccessLogCount,
                            onClickLogSummary = {
                                onClickLogSummary(it)
                            }
                        )
                    }
                    LoggableWorker.CHECK_SESSION -> {
                        LogSummaryRow(
                            title = "접속 정보 유효성 검사",
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
    val game = Game(
        type = hoYoLABGame,
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
                    text = stringResource(id = hoYoLABGame.gameNameStringResId()),
                    style = MaterialTheme.typography.labelMedium
                )

                Checkbox(
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