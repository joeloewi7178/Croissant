package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetScreen(
    navController: NavController,
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val selectableIntervals = createResinStatusWidgetViewModel.selectableIntervals
    val interval by createResinStatusWidgetViewModel.interval.collectAsState()
    val pagedAttendancesWithGames =
        createResinStatusWidgetViewModel.pagedAttendancesWithGames.collectAsLazyPagingItems()
    val checkedAttendanceIds = createResinStatusWidgetViewModel.checkedAttendanceIds
    val createResinStatusWidgetState by
    createResinStatusWidgetViewModel.createResinStatusWidgetState.collectAsState()
    val activity = LocalActivity.current

    LaunchedEffect(createResinStatusWidgetViewModel) {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    createResinStatusWidgetViewModel.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
        }
    }

    CreateResinStatusWidgetContent(
        appWidgetId = createResinStatusWidgetViewModel.appWidgetId,
        selectableIntervals = selectableIntervals,
        createResinStatusWidgetState = createResinStatusWidgetState,
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        checkedAttendanceIds = checkedAttendanceIds,
        interval = interval,
        onClickDone = createResinStatusWidgetViewModel::configureAppWidget,
        onIntervalChange = createResinStatusWidgetViewModel::setInterval
    )
}

@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetContent(
    appWidgetId: Int,
    selectableIntervals: List<Long>,
    createResinStatusWidgetState: Lce<List<Long>>,
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    checkedAttendanceIds: SnapshotStateList<Long>,
    interval: Long,
    onClickDone: () -> Unit,
    onIntervalChange: (Long) -> Unit
) {
    val activity = LocalActivity.current
    val (showProgressDialog, onShowProgressDialogChange) = mutableStateOf(false)

    LaunchedEffect(createResinStatusWidgetState) {
        when (createResinStatusWidgetState) {
            is Lce.Content -> {
                if (createResinStatusWidgetState.content.isNotEmpty()) {
                    onShowProgressDialogChange(false)

                    val resultValue = Intent().apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                    }
                    with(activity) {
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
            is Lce.Error -> {
                onShowProgressDialogChange(false)
            }
            Lce.Loading -> {
                onShowProgressDialogChange(true)
            }
        }
    }

    BackHandler {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.widget_configuration))
                }
            )
        },
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultDp)
                    .background(MaterialTheme.colorScheme.surface),
                enabled = checkedAttendanceIds.isNotEmpty(),
                onClick = onClickDone
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = Icons.Default.Done.name
                    )
                    Text(
                        text = stringResource(
                            id = R.string.account_selected,
                            checkedAttendanceIds.size
                        )
                    )
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
            )
        ) {
            item(key = "intervalTitle") {
                Text(
                    text = stringResource(id = R.string.refresh_interval),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item(key = "selectableIntervals") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    selectableIntervals.forEach {
                        Row(
                            modifier = Modifier.toggleable(
                                value = interval == it,
                                role = Role.RadioButton,
                                onValueChange = { checked ->
                                    if (checked) {
                                        onIntervalChange(it)
                                    }
                                }
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DefaultDp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            RadioButton(
                                selected = interval == it,
                                onClick = null
                            )

                            Text(text = stringResource(id = R.string.minute, it))
                        }
                    }
                }
            }

            item(key = "selectAccountTitle") {
                Text(
                    text = stringResource(id = R.string.account_to_check_resin),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (pagedAttendancesWithGames.isEmpty()) {
                item(key = "noAttendances") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DoubleDp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(0.3f),
                            imageVector = Icons.Default.Warning,
                            contentDescription = Icons.Default.Warning.name,
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = stringResource(id = R.string.no_attendance),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(id = R.string.make_attendance),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = pagedAttendancesWithGames,
                    key = { it.attendance.id }
                ) { item ->
                    if (item != null) {
                        AccountListItem(
                            item = item,
                            checkedAccounts = checkedAttendanceIds
                        )
                    } else {
                        AccountListItemPlaceholder()
                    }
                }
            }
        }

        if (showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    onShowProgressDialogChange(false)
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItem(
    item: AttendanceWithGames,
    checkedAccounts: SnapshotStateList<Long>
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checkedAccounts.contains(item.attendance.id),
                enabled = item.games.any { it.type == HoYoLABGame.GenshinImpact },
                role = Role.Checkbox,
                onValueChange = { checked ->
                    if (checked) {
                        checkedAccounts.add(item.attendance.id)
                    } else {
                        checkedAccounts.remove(item.attendance.id)
                    }
                }
            ),
        text = { Text(text = item.attendance.nickname)},
        trailing = {
            Checkbox(
                checked = checkedAccounts.contains(item.attendance.id),
                onCheckedChange = null
            )
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItemPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DoubleDp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                text = ""
            )

            Checkbox(
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                checked = false,
                onCheckedChange = null
            )
        }
    }
}