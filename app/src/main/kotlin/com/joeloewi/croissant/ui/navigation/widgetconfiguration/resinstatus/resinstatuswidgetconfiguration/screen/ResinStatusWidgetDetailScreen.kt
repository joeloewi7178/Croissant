package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ResinStatusWidgetDetailScreen(
    resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current
    val state by resinStatusWidgetDetailViewModel.collectAsState()
    var showProgressDialog by remember { mutableStateOf(false) }

    resinStatusWidgetDetailViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            ResinStatusWidgetDetailViewModel.SideEffect.DismissProgressDialog -> {
                showProgressDialog = false
            }

            ResinStatusWidgetDetailViewModel.SideEffect.ShowProgressDialog -> {
                showProgressDialog = true
            }

            ResinStatusWidgetDetailViewModel.SideEffect.FinishActivity -> {
                activity.finish()
            }
        }
    }

    ResinStatusWidgetDetailContent(
        state = state,
        showProgressDialog = showProgressDialog,
        onUpdateResinStatusWidget = resinStatusWidgetDetailViewModel::updateResinStatusWidget,
        onIntervalChange = resinStatusWidgetDetailViewModel::setInterval
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResinStatusWidgetDetailContent(
    state: ResinStatusWidgetDetailViewModel.State,
    showProgressDialog: Boolean,
    onUpdateResinStatusWidget: () -> Unit,
    onIntervalChange: (Long) -> Unit
) {
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
                    .navigationBarsPadding()
                    .padding(DefaultDp),
                onClick = onUpdateResinStatusWidget
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

        if (showProgressDialog) {
            ProgressDialog()
        }
    }
}