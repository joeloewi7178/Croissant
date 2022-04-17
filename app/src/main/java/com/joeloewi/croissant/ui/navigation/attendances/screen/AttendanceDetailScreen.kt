package com.joeloewi.croissant.ui.navigation.attendances.screen

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
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.common.navigationIconButton
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceDetailScreen(
    navController: NavController,
    attendanceDetailViewModel: AttendanceDetailViewModel = hiltViewModel()
) {
    val cookie by attendanceDetailViewModel.cookie.collectAsState()
    val hourOfDay by attendanceDetailViewModel.hourOfDay.collectAsState()
    val minute by attendanceDetailViewModel.minute.collectAsState()
    val nickname by attendanceDetailViewModel.nickname.collectAsState()
    val uid by attendanceDetailViewModel.uid.collectAsState()
    val checkedGame = attendanceDetailViewModel.checkedGames
    val connectedGames by attendanceDetailViewModel.connectedGames.collectAsState()
    val checkSessionWorkerSuccessLogCount by attendanceDetailViewModel.checkSessionWorkerSuccessLogCount.collectAsState()
    val checkSessionWorkerFailureLogCount by attendanceDetailViewModel.checkSessionWorkerFailureLogCount.collectAsState()
    val attendCheckInEventWorkerSuccessLogCount by attendanceDetailViewModel.attendCheckInEventWorkerSuccessLogCount.collectAsState()
    val attendCheckInEventWorkerFailureLogCount by attendanceDetailViewModel.attendCheckInEventWorkerFailureLogCount.collectAsState()

    AttendanceDetailContent(
        cookie = cookie,
        hourOfDay = hourOfDay,
        minute = minute,
        nickname = nickname,
        uid = uid,
        checkedGames = checkedGame,
        connectedGames = connectedGames,
        checkSessionWorkerSuccessLogCount = checkSessionWorkerSuccessLogCount,
        checkSessionWorkerFailureLogCount = checkSessionWorkerFailureLogCount,
        attendCheckInEventWorkerSuccessLogCount = attendCheckInEventWorkerSuccessLogCount,
        attendCheckInEventWorkerFailureLogCount = attendCheckInEventWorkerFailureLogCount,
        onHourOfDayChange = attendanceDetailViewModel::setHourOfDay,
        onMinuteChange = attendanceDetailViewModel::setMinute,
        navigationIconButton = navigationIconButton(navController = navController)
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceDetailContent(
    cookie: String,
    hourOfDay: Int,
    minute: Int,
    nickname: String,
    uid: Long,
    checkedGames: SnapshotStateList<HoYoLABGame>,
    connectedGames: Lce<List<GameRecord>>,
    checkSessionWorkerSuccessLogCount: Long,
    checkSessionWorkerFailureLogCount: Long,
    attendCheckInEventWorkerSuccessLogCount: Long,
    attendCheckInEventWorkerFailureLogCount: Long,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    navigationIconButton: @Composable () -> Unit
) {
    val scrollableState = rememberScrollState()
    val (timePicker, onTimePickerChange) = remember {
        mutableStateOf<TimePicker?>(
            null
        )
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

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "${nickname}의 출석 작업")
                },
                navigationIcon = navigationIconButton
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        DefaultDp
                    )
                ) {
                    Text(
                        text = "UID",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(text = "$uid")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        DefaultDp
                    )
                ) {
                    Text(
                        text = "닉네임",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(text = nickname)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        DefaultDp
                    )
                ) {
                    Text(
                        text = "연동 및 출석 작업 설정된 게임",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
                ) {
                    when (connectedGames) {
                        is Lce.Content -> {
                            items(
                                items = connectedGames.content,
                                key = { it.gameId }
                            ) { gameRecord ->
                                ConnectedGameItem(
                                    modifier = Modifier.animateItemPlacement(),
                                    gameRecord = gameRecord,
                                    checkedGames = checkedGames,
                                )
                            }
                        }

                        is Lce.Error -> {

                        }

                        Lce.Loading -> {
                            items(
                                items = IntArray(5) { it }.toTypedArray(),
                                key = { "placeholder${it}" }
                            ) {
                                ConnectedGameLoadingItem(modifier = Modifier.animateItemPlacement())
                            }
                        }
                    }
                }

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = Icons.Outlined.Refresh.name
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
                text = "실행 기록",
                style = MaterialTheme.typography.headlineSmall
            )

            Row(
                modifier = Modifier
                    .clickable { }
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
                            text = "출석 작업",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DefaultDp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = Icons.Outlined.Error.name,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "$attendCheckInEventWorkerFailureLogCount",
                            color = MaterialTheme.colorScheme.error
                        )

                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = Icons.Outlined.Done.name
                        )

                        Text(text = "$attendCheckInEventWorkerSuccessLogCount")
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NavigateNext,
                            contentDescription = Icons.Outlined.NavigateNext.name
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .clickable { }
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
                            text = "접속 정보 유효성 검사",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DefaultDp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Error,
                            contentDescription = Icons.Outlined.Error.name,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Text(
                            text = "$checkSessionWorkerFailureLogCount",
                            color = MaterialTheme.colorScheme.error
                        )

                        Icon(
                            imageVector = Icons.Outlined.Done,
                            contentDescription = Icons.Outlined.Done.name
                        )

                        Text(text = "$checkSessionWorkerSuccessLogCount")
                    }
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NavigateNext,
                            contentDescription = Icons.Outlined.NavigateNext.name
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGameItem(
    modifier: Modifier,
    gameRecord: GameRecord,
    checkedGames: SnapshotStateList<HoYoLABGame>
) {
    Card(
        onClick = {
            val checked = checkedGames.contains(gameRecord.hoYoLABGame)

            if (!checked) {
                checkedGames.add(gameRecord.hoYoLABGame)
            } else {
                checkedGames.remove(gameRecord.hoYoLABGame)
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
                Text(text = stringResource(id = gameRecord.hoYoLABGame.gameNameResourceId))

                Checkbox(
                    checked = checkedGames.contains(gameRecord.hoYoLABGame),
                    onCheckedChange = null
                )
            }

            AsyncImage(
                modifier = Modifier
                    .size(IconDp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gameRecord.hoYoLABGame.gameIconUrl)
                    .build(),
                contentDescription = null
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGameLoadingItem(
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