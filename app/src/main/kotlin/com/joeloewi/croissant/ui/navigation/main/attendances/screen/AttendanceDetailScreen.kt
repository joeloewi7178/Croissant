package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.LoggableWorker
import com.joeloewi.croissant.domain.entity.Game
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.TimeAndTimePicker
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.util.requestReview
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AttendanceDetailScreen(
    attendanceDetailViewModel: AttendanceDetailViewModel = hiltViewModel(),
    newCookie: () -> String,
    onNavigateUp: () -> Unit,
    onClickRefreshSession: () -> Unit,
    onClickLogSummary: (Long, LoggableWorker) -> Unit
) {
    val state by attendanceDetailViewModel.collectAsState()

    attendanceDetailViewModel.collectSideEffect {
        when (it) {
            is AttendanceDetailViewModel.AttendanceDetailSideEffect.Dialog -> {

            }

            is AttendanceDetailViewModel.AttendanceDetailSideEffect.NavigateToLog -> onClickLogSummary(
                it.attendanceId,
                it.loggableWorker
            )

            AttendanceDetailViewModel.AttendanceDetailSideEffect.NavigateUp -> onNavigateUp()
        }
    }

    AttendanceDetailContent(
        state = state,
        onNavigateUp = onNavigateUp,
        onClickRefreshSession = onClickRefreshSession,
        onClickLogSummary = attendanceDetailViewModel::onClickLogSummary,
        onHourOfDayChange = attendanceDetailViewModel::setHourOfDay,
        onMinuteChange = attendanceDetailViewModel::setMinute,
        onRefreshCookie = attendanceDetailViewModel::setCookie,
        onClickSave = attendanceDetailViewModel::updateAttendance,
        onConfirmDelete = attendanceDetailViewModel::deleteAttendance,
        onShowConfirmDeleteDialogChange = attendanceDetailViewModel::onShowConfirmDeleteDialogChange
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun AttendanceDetailContent(
    state: AttendanceDetailViewModel.AttendanceDetailState,
    onNavigateUp: () -> Unit,
    onClickRefreshSession: () -> Unit,
    onClickLogSummary: (LoggableWorker) -> Unit,
    onHourOfDayChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onRefreshCookie: (String) -> Unit,
    onClickSave: () -> Unit,
    onConfirmDelete: () -> Unit,
    onShowConfirmDeleteDialogChange: (Boolean) -> Unit
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    val activity = LocalActivity.current
    val snackbarHostState = remember { SnackbarHostState() }
    val pressSaveButton = stringResource(id = R.string.press_save_button_to_commit)
    val list by rememberUpdatedState(
        newValue = persistentListOf(
            stringResource(id = R.string.uid) to state.uid.toString(),
            stringResource(id = R.string.nickname) to state.nickname,
        )
    )

    /*LaunchedEffect(snackbarHostState) {
        snapshotFlow(newCookie).catch { }.collect {
            if (it.isNotEmpty()) {
                onRefreshCookie(it)
                snackbarHostState.showSnackbar(pressSaveButton)
            }
        }
    }*/

    LaunchedEffect(state.hasExecutedAtLeastOnce()) {
        if (state.hasExecutedAtLeastOnce()) {
            withContext(Dispatchers.Default) {
                requestReview(
                    activity = activity,
                    logMessage = "ExecutedAtLeastOnce"
                )
            }
        }
    }

    /*LaunchedEffect(Unit) {
        snapshotFlow(deleteAttendanceState).catch { }.collect {
            when (it) {
                is ILCE.Content -> {
                    showConfirmDeleteDialog = false
                    onNavigateUp()
                }

                is ILCE.Error -> {
                    showConfirmDeleteDialog = false
                    snackbarHostState.showSnackbar(activity.getString(R.string.error_occurred))
                }

                ILCE.Idle -> {

                }

                ILCE.Loading -> {
                    showConfirmDeleteDialog = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow(updateAttendanceState).catch { }.collect {
            when (it) {
                is ILCE.Content -> {
                    onNavigateUp()
                }

                else -> {

                }
            }
        }
    }*/

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.attendance_of_nickname, state.nickname)
                    )
                },
                navigationIcon = viewModelStoreOwner.navigationIconButton(
                    onClick = onNavigateUp
                ),
                actions = {
                    IconButton(
                        onClick = { onShowConfirmDeleteDialogChange(true) },
                        enabled = !state.isContentLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = Icons.Default.Delete.name
                        )
                    }

                    IconButton(
                        onClick = onClickSave,
                        enabled = !state.isContentLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = Icons.Default.Save.name
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .then(Modifier.padding(horizontal = DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
            ),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            item("userInformationHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.session),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("sessionInfos") {
                list.fastForEach {
                    SessionInfoRow(
                        key = it.first,
                        value = it.second
                    )
                }
            }

            item("changeCookie") {
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
                        Text(
                            text = stringResource(id = R.string.refresh_session),
                        )
                    }
                }
            }

            item("gamesHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.games_to_attend),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("games") {
                Column {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(space = DefaultDp)
                    ) {
                        items(
                            items = HoYoLABGame.entries.filter { it != HoYoLABGame.Unknown },
                            key = { it.name }
                        ) { item ->
                            val game = Game(type = item)

                            ConnectedGameListItem(
                                modifier = Modifier.animateItemPlacement(),
                                gameName = stringResource(id = item.gameNameStringResId()),
                                gameIconUrl = item.gameIconUrl,
                                checked = state.checkedGames.contains(game),
                                onCheckedChange = {

                                }
                            )
                        }
                    }
                }
            }

            item("timeSettingHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.schedule_time_setting),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item("timePicker") {
                TimeAndTimePicker(
                    modifier = Modifier.fillMaxWidth(),
                    hourOfDay = state.hourOfDay,
                    minute = state.minute,
                    onHourOfDayChange = onHourOfDayChange,
                    onMinuteChange = onMinuteChange
                )
            }

            item("logSummaryHeadline") {
                Text(
                    modifier = Modifier.padding(vertical = DefaultDp),
                    text = stringResource(id = R.string.execution_log_summary),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item(LoggableWorker.ATTEND_CHECK_IN_EVENT) {
                LogSummaryRow(
                    title = stringResource(id = R.string.attendance),
                    failureLogCount = state.attendCheckInEventWorkerFailureLogCount,
                    successLogCount = state.attendCheckInEventWorkerSuccessLogCount,
                    onClickLogSummary = {
                        onClickLogSummary(LoggableWorker.ATTEND_CHECK_IN_EVENT)
                    }
                )
            }

            item(LoggableWorker.CHECK_SESSION) {
                LogSummaryRow(
                    title = stringResource(id = R.string.session_validation),
                    failureLogCount = state.checkSessionWorkerFailureLogCount,
                    successLogCount = state.checkSessionWorkerSuccessLogCount,
                    onClickLogSummary = {
                        onClickLogSummary(LoggableWorker.CHECK_SESSION)
                    }
                )
            }
        }

        /*if (updateAttendanceState().isLoading) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }*/

        /*if (showConfirmDeleteDialog) {
            AlertDialog(
                onDismissRequest = { onShowConfirmDeleteDialogChange(false) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.alert))
                },
                text = {
                    Text(text = stringResource(id = R.string.confirm_delete_attendance_job))
                },
                confirmButton = {
                    TextButton(onClick = onConfirmDelete) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onShowConfirmDeleteDialogChange(false) }) {
                        Text(text = stringResource(id = R.string.dismiss))
                    }
                }
            )
        }*/
    }
}

@Composable
fun ConnectedGameListItem(
    modifier: Modifier,
    gameName: String,
    gameIconUrl: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = { onCheckedChange(checked) },
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
                    modifier = Modifier.weight(3f),
                    text = gameName,
                    style = MaterialTheme.typography.labelMedium
                )

                Checkbox(
                    modifier = Modifier.weight(1f),
                    checked = checked,
                    onCheckedChange = null
                )
            }

            AsyncImage(
                modifier = Modifier
                    .size(IconDp)
                    .clip(MaterialTheme.shapes.extraSmall),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gameIconUrl)
                    .build(),
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
                imageVector = Icons.AutoMirrored.Default.NavigateNext,
                contentDescription = Icons.AutoMirrored.Default.NavigateNext.name
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