package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.AttendanceLogsState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberAttendanceLogsState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsViewModel
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsScreen(
    navController: NavController,
    attendanceLogsViewModel: AttendanceLogsViewModel = hiltViewModel()
) {
    val attendanceLogsState = rememberAttendanceLogsState(
        navController = navController,
        attendanceLogsViewModel = attendanceLogsViewModel
    )

    AttendanceLogsContent(
        attendanceLogsState = attendanceLogsState,
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsContent(
    attendanceLogsState: AttendanceLogsState,
) {
    val pagedAttendanceLogs = attendanceLogsState.pagedAttendanceLogs
    val deleteAllState = attendanceLogsState.deleteAllState

    LaunchedEffect(deleteAllState) {
        when (deleteAllState) {
            is Lce.Content -> {
                val rowCount = deleteAllState.content
                if (rowCount != -1) {
                    attendanceLogsState.snackbarHostState.showSnackbar("${rowCount}개 삭제됨.")
                }
            }
            else -> {

            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = attendanceLogsState.snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = attendanceLogsState.previousBackStackEntry,
                    onClick = attendanceLogsState::onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = {
                            attendanceLogsState.showDeleteConfirmationDialog(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = Icons.Default.DeleteSweep.name
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        if (pagedAttendanceLogs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
                    text = stringResource(id = R.string.execution_log_is_empty),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .then(Modifier.padding(horizontal = DefaultDp)),
                verticalArrangement = Arrangement.spacedBy(DefaultDp)
            ) {
                items(
                    items = pagedAttendanceLogs,
                    key = { it.workerExecutionLog.id }
                ) { item ->
                    if (item != null) {
                        WorkerExecutionLogWithStateItem(
                            item = { item }
                        )
                    } else {
                        WorkerExecutionLogWithStateItemPlaceHolder()
                    }
                }
            }
        }

        if (attendanceLogsState.isShowingDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = {
                    attendanceLogsState.showDeleteConfirmationDialog(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            attendanceLogsState.onDeleteAll()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            attendanceLogsState.showDeleteConfirmationDialog(false)
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
                        text = stringResource(id = R.string.all_execution_log_will_be_deleted),
                        textAlign = TextAlign.Center
                    )
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun WorkerExecutionLogWithStateItem(
    item: () -> WorkerExecutionLogWithState
) {
    val currentItem by rememberUpdatedState(newValue = item())

    val colorByState by rememberUpdatedState(
        when (currentItem.workerExecutionLog.state) {
            WorkerExecutionLogState.SUCCESS -> {
                MaterialTheme.colorScheme.surfaceVariant
            }
            WorkerExecutionLogState.FAILURE -> {
                MaterialTheme.colorScheme.errorContainer
            }
        }
    )
    val textByState by rememberUpdatedState(
        when (currentItem.workerExecutionLog.state) {
            WorkerExecutionLogState.SUCCESS -> {
                stringResource(id = R.string.success)
            }
            WorkerExecutionLogState.FAILURE -> {
                stringResource(id = R.string.failure)
            }
        }
    )
    val readableTimestamp by remember(currentItem.workerExecutionLog.createdAt) {
        derivedStateOf {
            val dateTimeFormatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            val localDateTime =
                Instant.ofEpochMilli(currentItem.workerExecutionLog.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            dateTimeFormatter.format(localDateTime)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorByState
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DefaultDp),
        ) {
            Row {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
                ) {
                    Text(
                        text = textByState,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(text = readableTimestamp)

                    currentItem.successLog?.run {
                        Text(text = message)

                        Text(text = "$retCode")
                    }

                    currentItem.failureLog?.run {
                        Text(text = failureMessage)

                        Text(text = failureStackTrace)
                    }
                }

                Column {
                    AsyncImage(
                        modifier = Modifier
                            .size(IconDp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(currentItem.successLog?.gameName?.gameIconUrl)
                            .build(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun WorkerExecutionLogWithStateItemPlaceHolder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(DefaultDp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                text = ""
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                text = ""
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                text = ""
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
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
                model = ImageRequest.Builder(LocalContext.current)
                    .build(),
                contentDescription = null
            )
        }
    }
}
