package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.CreateResinStatusWidgetState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberCreateResinStatusWidgetState
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.COOKIE
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.getResultFromPreviousComposable
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import com.joeloewi.croissant.domain.entity.UserInfo

@Composable
fun CreateResinStatusWidgetScreen(
    navController: NavHostController,
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val createResinStatusWidgetState =
        rememberCreateResinStatusWidgetState(
            navController = navController,
            createResinStatusWidgetViewModel = createResinStatusWidgetViewModel
        )
    val getUserInfoState = createResinStatusWidgetState.getUserInfoState

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

    LaunchedEffect(createResinStatusWidgetState) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            createResinStatusWidgetState.onReceiveCookie(cookie = it)
        }
    }

    LaunchedEffect(getUserInfoState) {
        with(getUserInfoState) {
            when (this) {
                is Lce.Error -> {
                    createResinStatusWidgetState.snackbarHostState.showSnackbar(context.getString(R.string.error_occurred))
                }
                else -> {}
            }
        }
    }

    CreateResinStatusWidgetContent(
        createResinStatusWidgetState = createResinStatusWidgetState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateResinStatusWidgetContent(
    createResinStatusWidgetState: CreateResinStatusWidgetState,
) {
    val activity = LocalActivity.current
    val insertResinStatusWidgetState = createResinStatusWidgetState.insertResinStatusWidgetState
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
        snackbarHost = {
            SnackbarHost(hostState = createResinStatusWidgetState.snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = createResinStatusWidgetState::onClickAdd
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
                enabled = createResinStatusWidgetState.userInfos.isNotEmpty(),
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
                        text = stringResource(id = R.string.completed)
                    )
                }
            }
        }
    ) { innerPadding ->
        if (createResinStatusWidgetState.userInfos.isEmpty()) {
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
                    items = createResinStatusWidgetState.userInfos,
                    key = { it.first }
                ) { item ->
                    UserInfoListItem(
                        item = { item },
                    )
                }
            }
        }

        if (createResinStatusWidgetState.showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }

        if (createResinStatusWidgetState.showUserInfoProgressDialog) {
            ProgressDialog(
                title = { Text(text = stringResource(id = R.string.retrieving_data)) },
                onDismissRequest = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoListItem(
    item: () -> Pair<String, UserInfo>,
) {
    val currentItem by rememberUpdatedState(newValue = item())

    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineText = { Text(text = currentItem.second.nickname) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRecordListItemPlaceholder() {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        headlineText = {
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