package com.joeloewi.croissant.ui.navigation.attendances.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun AttendanceLogsContent(
    previousBackStackEntry: NavBackStackEntry?,
    pagedAttendanceLogs: LazyPagingItems<WorkerExecutionLogWithState>,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "실행 기록")
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = previousBackStackEntry,
                    onClick = onNavigateUp
                )
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
        colors = CardDefaults.cardColors(
            containerColor = colorByState
        ),
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
