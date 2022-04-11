package com.joeloewi.croissant.ui.navigation.attendances.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.joeloewi.croissant.data.local.model.AttendanceWithGames
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.viewmodel.AttendancesViewModel
import com.joeloewi.croissant.worker.AttendCheckInEventWorker

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun AttendancesScreen(
    navController: NavController,
    attendancesViewModel: AttendancesViewModel = hiltViewModel()
) {
    val pagedAttendancesWithGames =
        attendancesViewModel.pagedAttendanceWithGames.collectAsLazyPagingItems()

    AttendancesContent(
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        onCreateAttendanceClick = {
            navController.navigate(AttendancesDestination.CreateAttendanceScreen.route)
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun AttendancesContent(
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    onCreateAttendanceClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "출석 작업")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateAttendanceClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = Icons.Outlined.Add.name
                )
            }
        },
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(
                items = pagedAttendancesWithGames,
                key = { item -> item.attendance.id }
            ) { item ->
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item != null) {
                        AttendanceWithGamesItem(
                            attendanceWithGames = item,
                        )
                    } else {
                        AttendanceWithGamesItemPlaceholder()
                    }
                }

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
fun AttendanceWithGamesItem(
    attendanceWithGames: AttendanceWithGames,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        Text(
            text = "${attendanceWithGames.attendance.nickname}의 출석 작업",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val workInfo by WorkManager.getInstance(LocalContext.current)
                .getWorkInfoByIdLiveData(attendanceWithGames.attendance.attendCheckInEventWorkerId)
                .observeAsState()

            Text(
                text = "매일 ${
                    attendanceWithGames.attendance.hourOfDay.toString().padStart(2, '0')
                } : ${attendanceWithGames.attendance.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.headlineMedium
            )

            WorkInfoStateIndicator(workInfo = workInfo)
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            items(
                items = attendanceWithGames.games,
                key = { it.id }
            ) { game ->
                AsyncImage(
                    modifier = Modifier
                        .size(24.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(game.name.gameIconUrl)
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
        val localContext = LocalContext.current

        val workInfos by WorkManager.getInstance(LocalContext.current)
            .getWorkInfosForUniqueWorkLiveData(attendance.oneTimeAttendCheckInEventWorkerName.toString())
            .observeAsState()

        val isRunning = workInfos?.any { it.state == WorkInfo.State.RUNNING }

        IconButton(
            enabled = isRunning == false,
            onClick = {
                val oneTimeWork = OneTimeWorkRequestBuilder<AttendCheckInEventWorker>()
                    .setInputData(workDataOf(AttendCheckInEventWorker.ATTENDANCE_ID to attendance.id))
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()

                WorkManager.getInstance(localContext).beginUniqueWork(
                    attendance.oneTimeAttendCheckInEventWorkerName.toString(),
                    ExistingWorkPolicy.APPEND_OR_REPLACE,
                    oneTimeWork
                ).enqueue()
            }
        ) {
            AnimatedVisibility(
                visible = isRunning == false,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayCircle,
                    contentDescription = Icons.Outlined.PlayCircle.name
                )
            }

            AnimatedVisibility(
                visible = isRunning == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Pending,
                    contentDescription = Icons.Outlined.Pending.name
                )
            }
        }
    }
}

@Composable
fun AttendanceWithGamesItemPlaceholder() {
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
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
                space = 8.dp,
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
                    .size(24.dp)
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
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            items(
                items = IntArray(3) { it }.toTypedArray(),
                key = { "placeholder${it}" }
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(24.dp),
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
                .size(24.dp)
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

@Composable
fun WorkInfoStateIndicator(
    workInfo: WorkInfo?
) {
    if (workInfo != null) {
        when (workInfo.state) {

            //periodic work has only two states

            WorkInfo.State.ENQUEUED -> {
                Icon(
                    imageVector = Icons.Outlined.PendingActions,
                    contentDescription = Icons.Outlined.PendingActions.name
                )
            }

            WorkInfo.State.RUNNING -> {
                Icon(
                    imageVector = Icons.Outlined.Pending,
                    contentDescription = Icons.Outlined.Pending.name
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    contentDescription = Icons.Outlined.QuestionMark.name
                )
            }
        }
    } else {
        Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = Icons.Outlined.Error.name
        )
    }
}