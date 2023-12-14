package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.entity.UserInfo
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.withContext

@Composable
fun CreateResinStatusWidgetScreen(
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel = hiltViewModel()
) {
    val getInfoUserState by createResinStatusWidgetViewModel.getUserInfoState.collectAsStateWithLifecycle()
    val insertResinStatusWidgetState by createResinStatusWidgetViewModel.createResinStatusWidgetState.collectAsStateWithLifecycle()
    val appWidgetId by createResinStatusWidgetViewModel.appWidgetId.collectAsStateWithLifecycle()
    val userInfos = remember { createResinStatusWidgetViewModel.userInfos }
    val selectableIntervals =
        remember { createResinStatusWidgetViewModel.selectableIntervals.toImmutableList() }
    val interval by createResinStatusWidgetViewModel.interval.collectAsStateWithLifecycle()

    CreateResinStatusWidgetContent(
        getInfoUserState = { getInfoUserState },
        insertResinStatusWidgetState = { insertResinStatusWidgetState },
        appWidgetId = { appWidgetId },
        userInfos = userInfos,
        selectableIntervals = selectableIntervals,
        interval = { interval },
        onIntervalChange = createResinStatusWidgetViewModel::setInterval,
        onClickAdd = {

        },
        onClickDone = createResinStatusWidgetViewModel::configureAppWidget,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResinStatusWidgetContent(
    getInfoUserState: () -> Lce<UserInfo?>,
    insertResinStatusWidgetState: () -> Lce<List<Long>>,
    appWidgetId: () -> Int,
    userInfos: SnapshotStateList<Pair<String, UserInfo>>,
    selectableIntervals: ImmutableList<Long>,
    interval: () -> Long,
    onIntervalChange: (Long) -> Unit,
    onClickAdd: () -> Unit,
    onClickDone: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            snapshotFlow(insertResinStatusWidgetState).catch { }
                .filterIsInstance<Lce.Content<List<Long>>>().collect() {
                    if (it.content.isNotEmpty()) {
                        val resultValue = Intent().apply {
                            putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                appWidgetId()
                            )
                        }
                        with(activity) {
                            setResult(Activity.RESULT_OK, resultValue)
                            finish()
                        }
                    }
                }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            snapshotFlow(getInfoUserState).catch { }.filterIsInstance<Lce.Content<UserInfo?>>()
                .collect {
                    snackbarHostState.showSnackbar(context.getString(R.string.error_occurred))
                }
        }
    }

    LaunchedEffect(activity) {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId()
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
        }
    }

    /*LaunchedEffect(createResinStatusWidgetState) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            createResinStatusWidgetState.onReceiveCookie(cookie = it)
        }
    }*/

    BackHandler {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId()
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickAdd
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = Icons.Default.Add.name
                )
            }
        },
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultDp),
                enabled = userInfos.isNotEmpty(),
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
                        text = stringResource(id = R.string.completed)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (userInfos.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
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
                    text = stringResource(id = R.string.resin_status_widget_login_hoyolab),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = lazyListState,
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
                            val isSelected by remember(it) {
                                derivedStateOf { interval() == it }
                            }

                            Row(
                                modifier = Modifier.toggleable(
                                    value = isSelected,
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
                    items = userInfos,
                    key = { it.first }
                ) { item ->
                    UserInfoListItem(
                        item = { item },
                    )
                }
            }
        }

        if (insertResinStatusWidgetState().isLoading) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }

        if (getInfoUserState().isLoading) {
            ProgressDialog(
                title = { Text(text = stringResource(id = R.string.retrieving_data)) },
                onDismissRequest = {}
            )
        }
    }
}

@Composable
fun UserInfoListItem(
    item: () -> Pair<String, UserInfo>,
) {
    val currentItem by rememberUpdatedState(newValue = item())

    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineContent = { Text(text = currentItem.second.nickname) },
    )
}

@Composable
fun GameRecordListItemPlaceholder() {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineContent = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        shape = MaterialTheme.shapes.extraSmall,
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
                        shape = MaterialTheme.shapes.extraSmall,
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