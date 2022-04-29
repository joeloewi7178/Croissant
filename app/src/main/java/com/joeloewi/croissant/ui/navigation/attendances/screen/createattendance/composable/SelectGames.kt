package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.local.model.Game
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.common.ListItem
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.IconDp
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SelectGames(
    checkedGames: SnapshotStateList<Game>,
    connectedGames: Lce<List<GameRecord>>,
    onNextButtonClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val supportedGames = listOf(
        HoYoLABGame.HonkaiImpact3rd,
        HoYoLABGame.GenshinImpact
    )

    LaunchedEffect(connectedGames) {
        when (connectedGames) {
            is Lce.Content -> {
                connectedGames.content.forEach {
                    if (!supportedGames.contains(it.hoYoLABGame)) {
                        snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(message = "지원되지 않는 게임입니다. 추후 업데이트를 기다려주세요.")
                        }
                    }
                }
            }
            is Lce.Error -> {
                connectedGames.error.message?.let {
                    snackbarHostState.apply {
                        currentSnackbarData?.dismiss()
                        showSnackbar(it)
                    }
                }
            }
            Lce.Loading -> {

            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            val noGamesSelected = checkedGames.isEmpty()

            AnimatedVisibility(
                visible = connectedGames.content?.isNotEmpty() == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(noGamesSelected) {
                    if (noGamesSelected) {
                        snackbarHostState.apply {
                            currentSnackbarData?.dismiss()
                            showSnackbar(
                                message = "한 개 이상의 게임을 선택해주세요.",
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    } else {
                        snackbarHostState.currentSnackbarData?.dismiss()
                    }
                }

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !noGamesSelected,
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
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = Icons.Default.ArrowForward.name
                        )
                        Text(text = "다음 단계로")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = DoubleDp)
        ) {
            item(
                key = "headline"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "출석할 게임 선택하기",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item(
                key = "title"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "HoYoLAB 게임 선택하기",
                    style = MaterialTheme.typography.titleMedium
                )
            }


            item(
                key = "description"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "계정과 연동된 게임 목록 중에서 출석하고자 하는 게임을 선택해주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            when (connectedGames) {
                is Lce.Content -> {
                    if (connectedGames.content.isEmpty()) {
                        item(
                            key = "contentIsEmpty"
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .animateItemPlacement(),
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
                                    text = "HoYoLAB에 연동된 게임이 없습니다. 연동 후 다시 시도 해주세요.",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    } else {
                        items(
                            items = connectedGames.content,
                            key = { "${it.gameId}${it.region}" }
                        ) { gameRecord ->
                            ConnectedGamesContentListItem(
                                checkedGames = checkedGames,
                                gameRecord = gameRecord
                            )
                        }
                    }
                }

                is Lce.Error -> {
                    item(
                        key = "error"
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .animateItemPlacement(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = Icons.Default.Warning.name
                            )
                            Text(text = "오류가 발생했습니다. 다시 시도해주세요.")
                        }
                    }
                }

                Lce.Loading -> {
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

@ExperimentalMaterial3Api
@Composable
fun ConnectedGamesListItemPlaceholder() {
    ListItem(
        modifier = Modifier
            .fillMaxWidth(),
        icon = {
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
        trailing = {
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
        text = {
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
        },
        secondaryText = {
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
        }
    )
}

@ExperimentalMaterial3Api
@Composable
fun ConnectedGamesContentListItem(
    checkedGames: SnapshotStateList<Game>,
    gameRecord: GameRecord
) {
    val game = Game(
        roleId = gameRecord.gameRoleId,
        type = gameRecord.hoYoLABGame,
        region = gameRecord.region
    )

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checkedGames.contains(game),
                onValueChange = { checked ->
                    if (checked) {
                        checkedGames.add(
                            Game(
                                roleId = gameRecord.gameRoleId,
                                type = gameRecord.hoYoLABGame,
                                region = gameRecord.region
                            )
                        )
                    } else {
                        checkedGames.remove(game)
                    }
                }
            ),
        icon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier.size(IconDp),
                    model = ImageRequest.Builder(
                        LocalContext.current
                    )
                        .data(gameRecord.hoYoLABGame.gameIconUrl)
                        .build(),
                    contentDescription = null
                )
            }
        },
        trailing = {
            Checkbox(
                checked = checkedGames.contains(game),
                onCheckedChange = null
            )
        },
        text = {
            Text(text = stringResource(id = gameRecord.hoYoLABGame.gameNameResourceId))
        },
        secondaryText = {
            Text(text = "${gameRecord.regionName} (${gameRecord.region})")
        }
    )
}