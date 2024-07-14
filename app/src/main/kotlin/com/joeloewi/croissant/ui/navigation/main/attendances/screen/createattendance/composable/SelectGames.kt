package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.joeloewi.croissant.R
import com.joeloewi.croissant.core.data.model.Attendance
import com.joeloewi.croissant.core.data.model.GameRecord
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.gameNameStringResId
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.foundation.fade
import io.github.fornewid.placeholder.foundation.placeholder
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectGames(
    modifier: Modifier = Modifier,
    connectedGames: LCE<ImmutableList<Pair<HoYoLABGame, GameRecord?>>>,
    duplicatedAttendance: Attendance?,
    checkedGames: SnapshotStateList<Pair<HoYoLABGame, GameRecord?>>,
    onNextButtonClick: () -> Unit,
    onNavigateToAttendanceDetailScreen: (Long) -> Unit,
    onCancelCreateAttendance: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val containsNotSupportedGame = stringResource(id = R.string.contains_not_supported_game)
    val chooseAtLeastOneGame = stringResource(id = R.string.choose_at_least_one_game)
    val lazyListState = rememberLazyListState()
    var showDuplicateAlertDialog by remember(duplicatedAttendance) {
        mutableStateOf(duplicatedAttendance != null)
    }

    /*LaunchedEffect(Unit) {
        snapshotFlow(connectedGames).catch { }.collect {
            when (it) {
                is LCE.Content -> {
                    if (
                        it.content.any {
                            supportedGames.find { game -> game.gameId == it.gameId } == null
                        }
                    ) {
                        snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(message = containsNotSupportedGame)
                        }
                    }
                }

                is LCE.Error -> {
                    it.error.message?.let {
                        snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(it)
                        }
                    }
                }

                LCE.Loading -> {

                }
            }
        }
    }*/

    /*LaunchedEffect(checkedGames().isEmpty()) {
        snapshotFlow(connectedGames).catch { }.collect {
            when (it) {
                is LCE.Content -> {
                    val isEmpty = checkedGames().isEmpty()

                    if (isEmpty) {
                        snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(
                                message = chooseAtLeastOneGame,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    } else {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }

                else -> {

                }
            }
        }
    }*/

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            AnimatedVisibility(
                modifier = Modifier.navigationBarsPadding(),
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DefaultDp),
                    enabled = checkedGames.isNotEmpty(),
                    onClick = onNextButtonClick
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowForward,
                            contentDescription = Icons.AutoMirrored.Default.ArrowForward.name
                        )
                        Text(text = stringResource(id = R.string.next_step))
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.statusBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = DefaultDp),
            verticalArrangement = Arrangement.spacedBy(DefaultDp)
        ) {
            Text(
                text = stringResource(id = R.string.select_hoyolab_game_headline),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = stringResource(id = R.string.select_hoyolab_game_title),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(id = R.string.select_hoyolab_game_description),
                style = MaterialTheme.typography.bodyMedium
            )

            Crossfade(
                targetState = connectedGames,
                label = ""
            ) { state ->
                when (state) {
                    is LCE.Content -> {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = state.content,
                                key = { it.first.name }
                            ) { item ->
                                ConnectedGamesContentListItem(
                                    modifier = Modifier.animateItemPlacement(),
                                    connectedGame = item,
                                    checked = item in checkedGames,
                                    onCheckedChange = { checked ->
                                        if (!checked) {
                                            checkedGames.remove(item)
                                        } else {
                                            checkedGames.add(item)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    is LCE.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = Icons.Default.Error.name
                            )
                            Text(text = stringResource(id = R.string.error_occurred))
                        }
                    }

                    LCE.Loading -> {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            items(
                                items = IntArray(5) { it }.toTypedArray(),
                                key = { "placeholder${it}" }
                            ) {
                                ConnectedGamesListItemPlaceholder()
                            }
                        }
                    }
                }
            }
        }

        if (showDuplicateAlertDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDuplicateAlertDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            duplicatedAttendance?.id?.let {
                                showDuplicateAlertDialog = false
                                onNavigateToAttendanceDetailScreen(it)
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDuplicateAlertDialog = false
                            onCancelCreateAttendance()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.dismiss))
                    }
                },
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
                    Text(
                        text = stringResource(id = R.string.account_already_exists_fallback),
                        textAlign = TextAlign.Center
                    )
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }
    }
}

@Composable
fun ConnectedGamesListItemPlaceholder() {
    ListItem(
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
        },
        supportingContent = {
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
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(IconDp)
                    .placeholder(
                        visible = true,
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                model = null,
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@Composable
fun ConnectedGamesContentListItem(
    modifier: Modifier,
    connectedGame: Pair<HoYoLABGame, GameRecord?>,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                onValueChange = onCheckedChange
            ),
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(IconDp)
                        .clip(MaterialTheme.shapes.extraSmall),
                    model = connectedGame.first.gameIconUrl,
                    contentDescription = null
                )
            }
        },
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = null
            )
        },
        headlineContent = {
            Text(text = stringResource(id = connectedGame.first.gameNameStringResId()))
        },
        supportingContent = {
            connectedGame.second?.apply {
                if (regionName.isNotEmpty() && region.isNotEmpty()) {
                    Text(
                        text = "$regionName (${region})"
                    )
                }
            }
        }
    )
}