package com.joeloewi.croissant.ui.navigation.attendances.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.data.common.WorkerExecutionLogState
import com.joeloewi.croissant.data.local.model.relational.WorkerExecutionLogWithState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsViewModel
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsScreen(
    navController: NavController,
    attendanceLogsViewModel: AttendanceLogsViewModel = hiltViewModel()
) {
    val pagedAttendanceLogs = attendanceLogsViewModel.pagedAttendanceLogs.collectAsLazyPagingItems()

    AttendanceLogsContent(
        previousBackStackEntry = navController.previousBackStackEntry,
        pagedAttendanceLogs = pagedAttendanceLogs,
        onNavigateUp = {
            navController.navigateUp()
        },
        onDeleteAll = attendanceLogsViewModel::deleteAll
    )
}

@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsContent(
    previousBackStackEntry: NavBackStackEntry?,
    pagedAttendanceLogs: LazyPagingItems<WorkerExecutionLogWithState>,
    onNavigateUp: () -> Unit,
    onDeleteAll: () -> Unit
) {
    val (showDeleteConfirmationDialog, onShowDeleteConfirmationDialogChange) = remember {
        mutableStateOf(
            false
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "실행 기록")
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = previousBackStackEntry,
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = {
                            onShowDeleteConfirmationDialogChange(true)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = Icons.Default.DeleteSweep.name
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(DefaultDp)
        ) {
            items(
                items = pagedAttendanceLogs,
                key = { it.workerExecutionLog.id }
            ) { item ->
                if (item != null) {
                    WorkerExecutionLogWithStateItem(
                        item = item
                    )
                } else {
                    WorkerExecutionLogWithStateItemPlaceHolder()
                }
            }
        }

        if (showDeleteConfirmationDialog) {
            AlertDialog(
                onDismissRequest = {
                    onShowDeleteConfirmationDialogChange(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onShowDeleteConfirmationDialogChange(false)
                            onDeleteAll()
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onShowDeleteConfirmationDialogChange(false)
                        }
                    ) {
                        Text(text = "취소")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
                    )
                },
                title = {
                    Text(text = "경고")
                },
                text = {
                    Text(text = "현재 화면의 실행기록들이 모두 삭제됩니다. 계속하시겠습니까?")
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun WorkerExecutionLogWithStateItem(
    item: WorkerExecutionLogWithState
) {
    val colorByState = when (item.workerExecutionLog.state) {
        WorkerExecutionLogState.SUCCESS -> {
            MaterialTheme.colorScheme.surfaceVariant
        }
        WorkerExecutionLogState.FAILURE -> {
            MaterialTheme.colorScheme.errorContainer
        }
    }
    val textByState = when (item.workerExecutionLog.state) {
        WorkerExecutionLogState.SUCCESS -> {
            "성공"
        }
        WorkerExecutionLogState.FAILURE -> {
            "실패"
        }
    }
    val dateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(org.threeten.bp.format.FormatStyle.MEDIUM)
    val localDateTime =
        Instant.ofEpochMilli(item.workerExecutionLog.createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    val readableTimestamp = dateTimeFormatter.format(localDateTime)

    Card(
        modifier = Modifier.fillMaxWidth(),
        containerColor = colorByState
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

                    item.successLog?.run {
                        Text(text = message)

                        Text(text = "$retCode")
                    }

                    item.failureLog?.run {
                        Text(text = failureMessage)

                        Text(text = failureStackTrace)
                    }
                }

                Column {
                    AsyncImage(
                        modifier = Modifier
                            .size(IconDp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.successLog?.gameName?.gameIconUrl)
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
