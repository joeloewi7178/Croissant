package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce

@ExperimentalMaterial3Api
@ExperimentalMaterialApi
@Composable
fun SelectGames(
    connectedGames: Lce<List<GameRecord>>,
    checkedGames: SnapshotStateMap<HoYoLABGame, Boolean>,
    snackbarHostState: SnackbarHostState,
) {
    val supportedGames = listOf(
        HoYoLABGame.HonkaiImpact3rd,
        HoYoLABGame.GenshinImpact
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Text(
            text = "출석할 게임 선택하기",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "HoYoLAB 게임 선택하기",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        when (connectedGames) {
            is Lce.Content -> {
                if (connectedGames.content.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = Icons.Outlined.Warning.name
                        )
                        Spacer(modifier = Modifier.padding(vertical = 8.dp))
                        Text(text = "HoYoLAB에 연동된 게임이 없습니다. 연동 후 다시 시도 해주세요.")
                    }
                } else {

                    LaunchedEffect(connectedGames) {
                        connectedGames.content.forEach {
                            if (supportedGames.contains(it.hoYoLABGame)) {
                                checkedGames[it.hoYoLABGame] = true
                            } else {
                                snackbarHostState.showSnackbar(message = "지원되지 않는 게임입니다. 추후 업데이트를 기다려주세요.")
                            }
                        }
                    }

                    Text(
                        text = "계정과 연동된 게임 목록 중에서 출석하고자 하는 게임을 선택해주세요.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    AnimatedVisibility(
                        visible = checkedGames.values.all { !it },
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ) {
                            Row(
                                modifier = Modifier.padding(all = 8.dp),
                            ) {
                                Icon(
                                    modifier = Modifier.padding(all = 8.dp),
                                    imageVector = Icons.Outlined.Warning,
                                    contentDescription = Icons.Outlined.Warning.name
                                )
                                Text(
                                    modifier = Modifier.padding(all = 8.dp),
                                    text = "한 개 이상의 게임을 선택해주세요.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(vertical = 8.dp))

                    LazyColumn {
                        items(
                            items = connectedGames.content,
                            key = { it.gameId }
                        ) { gameRecord ->
                            ListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .toggleable(
                                        value = checkedGames.getOrDefault(
                                            gameRecord.hoYoLABGame,
                                            false
                                        ),
                                        onValueChange = { checked ->
                                            checkedGames[gameRecord.hoYoLABGame] =
                                                checked
                                        }
                                    ),
                                icon = {
                                    Row(
                                        modifier = Modifier.background(color = Color.Green),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier.size(24.dp),
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
                                        checked = checkedGames.getOrDefault(
                                            gameRecord.hoYoLABGame,
                                            false
                                        ),
                                        onCheckedChange = null
                                    )
                                },
                                text = {
                                    Text(text = stringResource(id = gameRecord.hoYoLABGame.gameNameResourceId))
                                },
                                secondaryText = {
                                    Text(text = gameRecord.regionName)
                                }
                            )
                        }
                    }
                }
            }

            is Lce.Error -> {

            }

            Lce.Loading -> {
                LazyColumn {
                    items(
                        items = IntArray(5) { it }.toTypedArray(),
                        key = { it }
                    ) {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            icon = {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .placeholder(
                                            visible = true,
                                            color = MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(4.dp),
                                            highlight = PlaceholderHighlight.fade(
                                                highlightColor = MaterialTheme.colorScheme.surface,
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
                                            shape = RoundedCornerShape(4.dp),
                                            highlight = PlaceholderHighlight.fade(
                                                highlightColor = MaterialTheme.colorScheme.surface,
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
                                            shape = RoundedCornerShape(4.dp),
                                            highlight = PlaceholderHighlight.fade(
                                                highlightColor = MaterialTheme.colorScheme.surface,
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
                                            shape = RoundedCornerShape(4.dp),
                                            highlight = PlaceholderHighlight.fade(
                                                highlightColor = MaterialTheme.colorScheme.surface,
                                            )
                                        ),
                                    text = ""
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}