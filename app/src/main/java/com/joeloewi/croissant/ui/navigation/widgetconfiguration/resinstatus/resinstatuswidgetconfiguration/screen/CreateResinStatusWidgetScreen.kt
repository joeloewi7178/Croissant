package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.CreateResinStatusWidgetState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberCreateResinStatusWidgetState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.relational.AttendanceWithGames

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetScreen(
    navController: NavController,
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val activity = LocalActivity.current
    val createResinStatusWidgetState =
        rememberCreateResinStatusWidgetState(createResinStatusWidgetViewModel)

    LaunchedEffect(activity) {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    createResinStatusWidgetState.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
        }
    }

    CreateResinStatusWidgetContent(
        createResinStatusWidgetState = createResinStatusWidgetState,
    )
}

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetContent(
    createResinStatusWidgetState: CreateResinStatusWidgetState,
) {
    val activity = LocalActivity.current
    val insertResinStatusWidgetState = createResinStatusWidgetState.insertResinStatusWidgetState
    val pagedAttendancesWithGames = createResinStatusWidgetState.pagedAttendancesWithGames
    val lazyListState = rememberLazyListState()

    LaunchedEffect(insertResinStatusWidgetState) {
        when (insertResinStatusWidgetState) {
            is Lce.Content -> {
                if (insertResinStatusWidgetState.content.isNotEmpty()) {
                    val resultValue = Intent().apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            createResinStatusWidgetState.appWidgetId
                        )
                    }
                    with(activity) {
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
            else -> {

            }
        }
    }

    BackHandler {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    createResinStatusWidgetState.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                enabled = createResinStatusWidgetState.isAttendanceIdItemSelected,
                onClick = createResinStatusWidgetState::onClickDone
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
                            createResinStatusWidgetState.checkedAttendanceIds.size
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        if (pagedAttendancesWithGames.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
                    .fillMaxSize()
                    .then(Modifier.padding(DoubleDp)),
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
        } else {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumedWindowInsets(innerPadding)
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
                        createResinStatusWidgetState.selectableIntervals.forEach {
                            val isSelected = createResinStatusWidgetState.interval == it

                            Row(
                                modifier = Modifier.toggleable(
                                    value = isSelected,
                                    role = Role.RadioButton,
                                    onValueChange = { checked ->
                                        if (checked) {
                                            createResinStatusWidgetState.onIntervalChange(it)
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
                                    selected = isSelected,
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

                items(
                    items = pagedAttendancesWithGames,
                    key = { it.attendance.id }
                ) { item ->
                    if (item != null) {
                        AccountListItem(
                            item = { item },
                            checkedAccounts = createResinStatusWidgetState.checkedAttendanceIds
                        )
                    } else {
                        AccountListItemPlaceholder()
                    }
                }
            }
        }

        if (createResinStatusWidgetState.showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItem(
    item: () -> AttendanceWithGames,
    checkedAccounts: SnapshotStateList<Long>
) {
    val currentItem by rememberUpdatedState(newValue = item())

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checkedAccounts.contains(currentItem.attendance.id),
                enabled = currentItem.games.any { it.type == HoYoLABGame.GenshinImpact },
                role = Role.Checkbox,
                onValueChange = { checked ->
                    if (checked) {
                        checkedAccounts.add(currentItem.attendance.id)
                    } else {
                        checkedAccounts.remove(currentItem.attendance.id)
                    }
                }
            ),
        headlineText = { Text(text = currentItem.attendance.nickname) },
        trailingContent = {
            Checkbox(
                checked = checkedAccounts.contains(currentItem.attendance.id),
                onCheckedChange = null
            )
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItemPlaceholder() {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineText = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                text = ""
            )
        },
        trailingContent = {
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
    )
}