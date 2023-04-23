package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.ResinStatusWidgetDetailState
import com.joeloewi.croissant.state.rememberResinStatusWidgetDetailState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel

@Composable
fun ResinStatusWidgetDetailScreen(
    navController: NavHostController,
    resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel = hiltViewModel()
) {
    val resinStatusWidgetDetailState = rememberResinStatusWidgetDetailState(
        resinStatusWidgetDetailViewModel = resinStatusWidgetDetailViewModel
    )

    ResinStatusWidgetDetailContent(
        resinStatusWidgetDetailState = resinStatusWidgetDetailState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResinStatusWidgetDetailContent(
    resinStatusWidgetDetailState: ResinStatusWidgetDetailState
) {
    val activity = LocalActivity.current
    val updateResinStatusWidgetState = resinStatusWidgetDetailState.updateResinStatusWidgetState

    LaunchedEffect(updateResinStatusWidgetState) {
        when (updateResinStatusWidgetState) {
            is Lce.Content -> {
                if (updateResinStatusWidgetState.content != 0) {
                    activity.finish()
                }
            }

            else -> {}
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
                    .navigationBarsPadding()
                    .padding(DefaultDp),
                onClick = resinStatusWidgetDetailState::updateResinStatusWidget
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
                resinStatusWidgetDetailState.selectableIntervals.forEach {
                    val isSelected = resinStatusWidgetDetailState.interval == it

                    Row(
                        modifier = Modifier.toggleable(
                            value = isSelected,
                            role = Role.RadioButton,
                            onValueChange = { checked ->
                                if (checked) {
                                    resinStatusWidgetDetailState.onIntervalChange(it)
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

        if (resinStatusWidgetDetailState.showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {}
            )
        }
    }
}