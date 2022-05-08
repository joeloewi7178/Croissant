package com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.ListItem
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.Attendance
import com.joeloewi.domain.entity.Game
import com.joeloewi.domain.entity.GameRecord
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterial3Api
@Composable
fun SelectGames(
    duplicatedAttendance: Attendance?,
    checkedGames: SnapshotStateList<Game>,
    connectedGames: Lce<List<GameRecord>>,
    onNextButtonClick: () -> Unit,
    onNavigateToAttendanceDetailScreen: (Long) -> Unit,
    onCancelCreateAttendance: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val (showDuplicatedAttendanceDialog, onShowDuplicatedAttendanceDialogChange) = remember {
        mutableStateOf(
            false to 0L
        )
    }
    val supportedGames = listOf(
        HoYoLABGame.HonkaiImpact3rd,
        HoYoLABGame.GenshinImpact,
        HoYoLABGame.TearsOfThemis
    )

    LaunchedEffect(connectedGames) {
        when (connectedGames) {
            is Lce.Content -> {
                if (connectedGames.content.any {
                        !supportedGames.contains(
                            HoYoLABGame.findByGameId(
                                it.gameId
                            )
                        )
                    }) {
                    snackbarHostState.apply {
                        currentSnackbarData?.dismiss()
                        showSnackbar(message = "지원되지 않는 게임이 있습니다. 추후 업데이트를 기다려주세요.")
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

    LaunchedEffect(duplicatedAttendance) {
        if (duplicatedAttendance != null) {
            onShowDuplicatedAttendanceDialogChange(true to duplicatedAttendance.id)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            val noGamesSelected = checkedGames.isEmpty()

            AnimatedVisibility(
                visible = !connectedGames.isLoading && connectedGames.error == null,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
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
                .padding(innerPadding)
        ) {
            item(
                key = "headline"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "출석할 게임 선택하기",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.padding(DefaultDp))
            }

            item(
                key = "title"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "HoYoLAB 게임 선택하기",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.padding(DefaultDp))
            }


            item(
                key = "description"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "계정과 연동된 게임 목록 중에서 출석하고자 하는 게임을 선택해주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.padding(DefaultDp))
            }

            when (connectedGames) {
                is Lce.Content -> {
                    items(
                        items = HoYoLABGame.values().filter { it != HoYoLABGame.Unknown },
                        key = { it.name }
                    ) { item ->
                        ConnectedGamesContentListItem(
                            modifier = Modifier.animateItemPlacement(),
                            checkedGames = checkedGames,
                            hoYoLABGame = item,
                            gameRecord = connectedGames.content.find { it.gameId == item.gameId }
                                ?: GameRecord()
                        )
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
                                imageVector = Icons.Default.Error,
                                contentDescription = Icons.Default.Error.name
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

        if (showDuplicatedAttendanceDialog.first) {
            AlertDialog(
                onDismissRequest = {
                    onShowDuplicatedAttendanceDialogChange(false to 0L)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onNavigateToAttendanceDetailScreen(showDuplicatedAttendanceDialog.second)
                            onShowDuplicatedAttendanceDialogChange(false to 0L)
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onShowDuplicatedAttendanceDialogChange(false to 0L)
                            onCancelCreateAttendance()
                        }
                    ) {
                        Text(text = "취소")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
                    )
                },
                title = {
                    Text(text = "알림")
                },
                text = {
                    Text(text = "입력하신 계정은 이미 출석 작업에 등록되어있어 출석 작업 만들기를 중단합니다. 해당 출석 작업의 상세화면으로 이동하시겠습니까?")
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
    modifier: Modifier,
    checkedGames: SnapshotStateList<Game>,
    hoYoLABGame: HoYoLABGame,
    gameRecord: GameRecord
) {
    val game = Game(
        roleId = gameRecord.gameRoleId,
        type = hoYoLABGame,
        region = gameRecord.region
    )

    val enabled =
        hoYoLABGame == HoYoLABGame.TearsOfThemis ||
                gameRecord.gameId != GameRecord.INVALID_GAME_ID

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
        icon = {
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
        trailing = {
            Checkbox(
                checked = checkedGames.contains(game),
                onCheckedChange = null
            )
        },
        text = {
            Text(text = stringResource(id = hoYoLABGame.gameNameStringResId()))
        },
        secondaryText = {
            with(gameRecord) {
                if (regionName.isNotEmpty() && region.isNotEmpty()) {
                    Text(text = "$regionName (${region})")
                }
            }
        }
    )
}