package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.WorkerExecutionLogState
import com.joeloewi.croissant.domain.entity.relational.WorkerExecutionLogWithState
import com.joeloewi.croissant.state.AttendanceLogsDayState
import com.joeloewi.croissant.state.rememberAttendanceLogsDayState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsDayViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun AttendanceLogsDayScreen(
    navController: NavHostController,
    attendanceLogsDayViewModel: AttendanceLogsDayViewModel
) {
    val attendanceLogsDayState = rememberAttendanceLogsDayState(
        navController = navController,
        attendanceLogsDayViewModel = attendanceLogsDayViewModel
    )

    AttendanceLogsDayContent(
        attendanceLogsDayState = attendanceLogsDayState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceLogsDayContent(
    attendanceLogsDayState: AttendanceLogsDayState
) {
    val pagedAttendanceLogs = attendanceLogsDayState.pagedAttendanceLogs

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = attendanceLogsDayState.previousBackStackEntry,
                    onClick = attendanceLogsDayState::onNavigateUp
                ),
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            items(
                items = pagedAttendanceLogs,
                key = { it.workerExecutionLog.id }
            ) { item ->
                if (item != null) {
                    WorkerExecutionLogWithStateItem(item = { item })
                } else {
                    WorkerExecutionLogWithStateItemPlaceHolder()
                }
            }
        }
    }
}

@Composable
private fun WorkerExecutionLogWithStateItem(
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DefaultDp, vertical = HalfDp),
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
                            .clip(MaterialTheme.shapes.extraSmall)
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

@Composable
private fun WorkerExecutionLogWithStateItemPlaceHolder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DefaultDp, vertical = HalfDp),
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
                        shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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