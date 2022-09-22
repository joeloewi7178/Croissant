package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.CreateAttendanceState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberSelectGamesState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.entity.GameRecord
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalLifecycleComposeApi
@ObsoleteCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun SelectGames(
    modifier: Modifier,
    createAttendanceState: CreateAttendanceState
) {
    val selectGamesState = rememberSelectGamesState(
        createAttendanceState = createAttendanceState,
        supportedGames = listOf(
            HoYoLABGame.HonkaiImpact3rd,
            HoYoLABGame.GenshinImpact,
            HoYoLABGame.TearsOfThemis
        ).toImmutableList()
    )
    val connectedGames = selectGamesState.connectedGames
    val duplicatedAttendance = selectGamesState.duplicatedAttendance
    val containsNotSupportedGame = stringResource(id = R.string.contains_not_supported_game)
    val chooseAtLeastOneGame = stringResource(id = R.string.choose_at_least_one_game)
    val lazyListState = rememberLazyListState()

    LaunchedEffect(connectedGames) {
        when (connectedGames) {
            is Lce.Content -> {
                if (connectedGames.content.any { !selectGamesState.isSupportedGame(it.gameId) }) {
                    selectGamesState.snackbarHostState.apply {
                        currentSnackbarData?.dismiss()
                        showSnackbar(message = containsNotSupportedGame)
                    }
                }
            }
            is Lce.Error -> {
                connectedGames.error.message?.let {
                    selectGamesState.snackbarHostState.apply {
                        currentSnackbarData?.dismiss()
                        showSnackbar(it)
                    }
                }
            }
            Lce.Loading -> {

            }
        }
    }

    LaunchedEffect(duplicatedAttendance) {
        if (duplicatedAttendance != null) {
            selectGamesState.onShowDuplicatedAttendanceDialogChange(true to duplicatedAttendance.id)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = selectGamesState.snackbarHostState)
        },
        bottomBar = {
            AnimatedVisibility(
                visible = !connectedGames.isLoading && connectedGames.error == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(selectGamesState.noGamesSelected) {
                    if (selectGamesState.noGamesSelected) {
                        selectGamesState.snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(
                                message = chooseAtLeastOneGame,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    } else {
                        selectGamesState.snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    enabled = !selectGamesState.noGamesSelected,
                    onClick = selectGamesState::onNextButtonClick
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = Icons.Default.ArrowForward.name
                        )
                        Text(text = stringResource(id = R.string.next_step))
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        Column(
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

            when (connectedGames) {
                is Lce.Content -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        items(
                            items = selectGamesState.supportedGames,
                            key = { it.name }
                        ) { item ->
                            ConnectedGamesContentListItem(
                                modifier = Modifier.animateItemPlacement(),
                                checkedGames = selectGamesState.checkedGames,
                                hoYoLABGame = item,
                                gameRecord = {
                                    connectedGames.content.find { it.gameId == item.gameId }
                                        ?: GameRecord()
                                }
                            )
                        }
                    }
                }
                is Lce.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
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
                Lce.Loading -> {
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

        if (selectGamesState.showDuplicatedAttendanceDialog.first) {
            AlertDialog(
                onDismissRequest = {
                    selectGamesState.onShowDuplicatedAttendanceDialogChange(false to 0L)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            with(selectGamesState) {
                                onNavigateToAttendanceDetailScreen(showDuplicatedAttendanceDialog.second)
                                onShowDuplicatedAttendanceDialogChange(false to 0L)
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            with(selectGamesState) {
                                onShowDuplicatedAttendanceDialogChange(false to 0L)
                                onCancelCreateAttendance()
                            }
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

@ExperimentalMaterial3Api
@Composable
fun ConnectedGamesListItemPlaceholder() {
    ListItem(
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
        },
        supportingText = {
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
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .size(IconDp)
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                model = ImageRequest.Builder(
                    LocalContext.current
                ).build(),
                contentDescription = null
            )
        },
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGamesContentListItem(
    modifier: Modifier,
    checkedGames: SnapshotStateList<Game>,
    hoYoLABGame: HoYoLABGame,
    gameRecord: (HoYoLABGame) -> GameRecord
) {
    val currentGameRecord by rememberUpdatedState(newValue = gameRecord(hoYoLABGame))
    val game by remember(hoYoLABGame, gameRecord) {
        derivedStateOf {
            Game(
                roleId = currentGameRecord.gameRoleId,
                type = hoYoLABGame,
                region = currentGameRecord.region
            )
        }
    }

    val enabled by remember(hoYoLABGame, gameRecord) {
        derivedStateOf {
            hoYoLABGame == HoYoLABGame.TearsOfThemis ||
                    currentGameRecord.gameId != GameRecord.INVALID_GAME_ID
        }
    }

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .alpha(
                if (enabled) {
                    1.0f
                } else {
                    0.3f
                }
            )
            .toggleable(
                value = checkedGames.contains(game),
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = { checked ->
                    if (checked) {
                        checkedGames.add(game)
                    } else {
                        checkedGames.remove(game)
                    }
                }
            ),
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier.size(IconDp),
                    model = ImageRequest.Builder(
                        LocalContext.current
                    )
                        .data(hoYoLABGame.gameIconUrl)
                        .build(),
                    contentDescription = null
                )
            }
        },
        trailingContent = {
            Checkbox(
                checked = checkedGames.contains(game),
                onCheckedChange = null
            )
        },
        headlineText = {
            Text(text = stringResource(id = hoYoLABGame.gameNameStringResId()))
        },
        supportingText = {
            with(currentGameRecord) {
                if (regionName.isNotEmpty() && region.isNotEmpty()) {
                    Text(
                        text = "$regionName (${region})"
                    )
                }
            }
        }
    )
}