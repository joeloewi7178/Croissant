package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.data.model.WorkerExecutionLogState
import com.joeloewi.croissant.core.data.model.relational.WorkerExecutionLogWithState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.AttendanceLogsDayViewModel
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.foundation.fade
import io.github.fornewid.placeholder.foundation.placeholder
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun AttendanceLogsDayScreen(
    attendanceLogsDayViewModel: AttendanceLogsDayViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val pagedAttendanceLogs =
        attendanceLogsDayViewModel.pagedAttendanceLogs.collectAsLazyPagingItems()

    attendanceLogsDayViewModel.collectSideEffect {
        when (it) {
            AttendanceLogsDayViewModel.SideEffect.NavigateUp -> onNavigateUp()
        }
    }

    AttendanceLogsDayContent(
        pagedAttendanceLogs = pagedAttendanceLogs,
        onNavigateUp = attendanceLogsDayViewModel::onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceLogsDayContent(
    pagedAttendanceLogs: LazyPagingItems<WorkerExecutionLogWithState>,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.execution_log))
                },
                navigationIcon = LocalViewModelStoreOwner.current.navigationIconButton(
                    onClick = onNavigateUp
                ),
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            items(
                count = pagedAttendanceLogs.itemCount,
                key = pagedAttendanceLogs.itemKey { it.workerExecutionLog.id },
                contentType = pagedAttendanceLogs.itemContentType { it.workerExecutionLog.state }
            ) { index ->
                val item = pagedAttendanceLogs[index]

                if (item != null) {
                    WorkerExecutionLogWithStateItem(item = item)
                } else {
                    WorkerExecutionLogWithStateItemPlaceHolder()
                }
            }
        }
    }
}

@Composable
private fun WorkerExecutionLogWithStateItem(
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
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            val localDateTime =
                Instant.ofEpochMilli(item.workerExecutionLog.createdAt)
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
                            .clip(MaterialTheme.shapes.extraSmall)
                            .size(IconDp),
                        model = item.successLog?.gameName?.gameIconUrl,
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
                model = null,
                contentDescription = null
            )
        }
    }
}