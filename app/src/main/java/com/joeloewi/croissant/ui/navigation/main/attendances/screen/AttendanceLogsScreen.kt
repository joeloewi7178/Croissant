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
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsViewModel
import com.joeloewi.domain.common.WorkerExecutionLogState
import com.joeloewi.domain.entity.relational.WorkerExecutionLogWithState
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
                    Text(text = stringResource(id = R.string.execution_log))
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
                            item = item
                        )
                    } else {
                        WorkerExecutionLogWithStateItemPlaceHolder()
                    }
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
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onShowDeleteConfirmationDialogChange(false)
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
    item: WorkerExecutionLogWithState
) {
    val colorByState by rememberUpdatedState(
        when (item.workerExecutionLog.state) {
            WorkerExecutionLogState.SUCCESS -> {
                MaterialTheme.colorScheme.surfaceVariant
            }
            WorkerExecutionLogState.FAILURE -> {
                MaterialTheme.colorScheme.errorContainer
            }
        }
    )
    val textByState by rememberUpdatedState(
        when (item.workerExecutionLog.state) {
            WorkerExecutionLogState.SUCCESS -> {
                stringResource(id = R.string.success)
            }
            WorkerExecutionLogState.FAILURE -> {
                stringResource(id = R.string.failure)
            }
        }
    )
    val readableTimestamp by remember(item.workerExecutionLog.createdAt) {
        derivedStateOf {
            val dateTimeFormatter =
                DateTimeFormatter.ofLocalizedDateTime(org.threeten.bp.format.FormatStyle.MEDIUM)
            val localDateTime =
                Instant.ofEpochMilli(item.workerExecutionLog.createdAt)
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
