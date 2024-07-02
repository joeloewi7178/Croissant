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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.data.model.UserInfo
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun CreateResinStatusWidgetScreen(
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel = hiltViewModel(),
    newCookie: () -> String,
    onClickAdd: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val state by createResinStatusWidgetViewModel.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showProgressDialog by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) }

    LaunchedEffect(activity) {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    state.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
        }
    }

    BackHandler {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    state.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()
        }
    }

    createResinStatusWidgetViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            CreateResinStatusWidgetViewModel.SideEffect.DismissProgressDialog -> {
                showProgressDialog = false to null
            }

            is CreateResinStatusWidgetViewModel.SideEffect.FinishActivity -> {
                val resultValue = Intent().apply {
                    putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        sideEffect.appWidgetId
                    )
                }
                with(activity) {
                    setResult(Activity.RESULT_OK, resultValue)
                    finish()
                }
            }

            is CreateResinStatusWidgetViewModel.SideEffect.ShowProgressDialog -> {
                showProgressDialog =
                    true to sideEffect.textResourceId?.let { context.getString(it) }
            }

            CreateResinStatusWidgetViewModel.SideEffect.ShowSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(context.getString(R.string.error_occurred))
                }
            }
        }
    }

    CreateResinStatusWidgetContent(
        state = state,
        snackbarHostState = snackbarHostState,
        showProgressDialog = showProgressDialog,
        newCookie = newCookie,
        onIntervalChange = createResinStatusWidgetViewModel::setInterval,
        onClickAdd = onClickAdd,
        onClickDone = createResinStatusWidgetViewModel::configureAppWidget,
        onReceiveCookie = createResinStatusWidgetViewModel::onReceiveCookie
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResinStatusWidgetContent(
    state: CreateResinStatusWidgetViewModel.State,
    snackbarHostState: SnackbarHostState,
    showProgressDialog: Pair<Boolean, String?>,
    newCookie: () -> String,
    onIntervalChange: (Long) -> Unit,
    onClickAdd: () -> Unit,
    onClickDone: (
        appWidgetId: Int,
        interval: Long,
        userInfos: List<Pair<String, UserInfo>>
    ) -> Unit,
    onReceiveCookie: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(newCookie()) {
        if (newCookie().isNotEmpty()) {
            onReceiveCookie(newCookie())
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
                    .padding(DefaultDp)
                    .navigationBarsPadding(),
                enabled = state.userInfos.isEmpty(),
                onClick = {
                    onClickDone(
                        state.appWidgetId.toInt(),
                        state.interval,
                        state.userInfos
                    )
                }
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
        if (state.userInfos.isEmpty()) {
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
                        state.selectableIntervals.forEach {
                            val isSelected by remember(it, state.interval) {
                                derivedStateOf { state.interval == it }
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
                    items = state.userInfos,
                    key = { it.first }
                ) { item ->
                    UserInfoListItem(
                        item = item,
                    )
                }
            }
        }

        if (showProgressDialog.first) {
            ProgressDialog(showProgressDialog.second?.let {
                {
                    Text(text = stringResource(id = R.string.retrieving_data))
                }
            })
        }
    }
}

@Composable
fun UserInfoListItem(
    item: Pair<String, UserInfo>,
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineContent = { Text(text = item.second.nickname) },
    )
}