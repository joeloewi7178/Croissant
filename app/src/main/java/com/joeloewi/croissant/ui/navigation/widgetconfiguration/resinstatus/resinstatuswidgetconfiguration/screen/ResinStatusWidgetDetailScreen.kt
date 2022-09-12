package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel

@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetDetailScreen(
    navController: NavController,
    resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel = hiltViewModel()
) {
    val selectableIntervals = remember { resinStatusWidgetDetailViewModel.selectableIntervals }
    val interval by resinStatusWidgetDetailViewModel.interval.collectAsStateWithLifecycle()
    val updateResinStatusWidgetState by resinStatusWidgetDetailViewModel.updateResinStatusWidgetState.collectAsStateWithLifecycle()

    ResinStatusWidgetDetailContent(
        selectableIntervals = selectableIntervals,
        interval = interval,
        updateResinStatusWidgetState = updateResinStatusWidgetState,
        onIntervalChange = resinStatusWidgetDetailViewModel::setInterval,
        onClickDone = resinStatusWidgetDetailViewModel::updateResinStatusWidget
    )
}

@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetDetailContent(
    selectableIntervals: List<Long>,
    interval: Long,
    updateResinStatusWidgetState: Lce<Int>,
    onIntervalChange: (Long) -> Unit,
    onClickDone: () -> Unit
) {
    val activity = LocalActivity.current
    val (showProgressDialog, onShowProgressDialogChange) = mutableStateOf(false)

    LaunchedEffect(updateResinStatusWidgetState) {
        when (updateResinStatusWidgetState) {
            is Lce.Content -> {
                if (updateResinStatusWidgetState.content != 0) {
                    onShowProgressDialogChange(false)
                    activity.finish()
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
                    Text(text = stringResource(id = R.string.update_completed))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                text = stringResource(id = R.string.refresh_interval),
                style = MaterialTheme.typography.titleMedium
            )

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

        if (showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    onShowProgressDialogChange(false)
                }
            )
        }
    }
}