package com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.RedemptionCodesState
import com.joeloewi.croissant.state.rememberRedemptionCodesState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel
import com.joeloewi.croissant.domain.common.HoYoLABGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun RedemptionCodesScreen(
    navController: NavHostController,
    redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()
) {
    val redemptionCodesState =
        rememberRedemptionCodesState(redemptionCodesViewModel = redemptionCodesViewModel)

    RedemptionCodesContent(
        redemptionCodesState = redemptionCodesState,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
@Composable
private fun RedemptionCodesContent(
    redemptionCodesState: RedemptionCodesState,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = redemptionCodesState.snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.RedemptionCodes.resourceId))
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) { innerPadding ->
        val pullRefreshState = redemptionCodesState.swipeRefreshState

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            with(redemptionCodesState.hoYoLABGameRedemptionCodesState) {
                when (this) {
                    is Lce.Content -> {
                        RedemptionCodes(
                            hoYoLABGameRedemptionCodes = content.toImmutableList(),
                            expandedItems = redemptionCodesState.expandedItems,
                        )
                    }
                    is Lce.Error -> {
                        RedemptionCodesError(onRefresh = redemptionCodesState::onRefresh)
                    }
                    Lce.Loading -> {
                        RedemptionCodesLoading()
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = redemptionCodesState.hoYoLABGameRedemptionCodesState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface)
            )
        }
    }
}

@Composable
private fun RedemptionCodesLoading() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(
            items = IntArray(3) { it }.toTypedArray(),
            key = { "placeholder${it}" }
        ) {
            RedemptionCodeListItemPlaceholder()
        }
    }
}

@Composable
private fun RedemptionCodesError(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(Modifier.padding(DoubleDp)),
        verticalArrangement = Arrangement.spacedBy(DefaultDp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(0.3f),
            imageVector = Icons.Default.Error,
            contentDescription = Icons.Default.Error.name,
            tint = MaterialTheme.colorScheme.primaryContainer
        )
        Text(
            text = stringResource(id = R.string.error_occurred),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.due_to_site_policy),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRefresh) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    DefaultDp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = Icons.Default.Refresh.name
                )
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RedemptionCodes(
    hoYoLABGameRedemptionCodes: ImmutableList<Pair<HoYoLABGame, AnnotatedString>>,
    expandedItems: SnapshotStateList<HoYoLABGame>,
) {
    Box(modifier = Modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            items(
                items = hoYoLABGameRedemptionCodes,
                key = { it.first.gameId }
            ) { item ->
                when (item.first) {
                    HoYoLABGame.Unknown, HoYoLABGame.TearsOfThemis -> {

                    }

                    else -> {
                        RedemptionCodeListItem(
                            modifier = Modifier.animateItemPlacement(),
                            item = item,
                            expandedItems = expandedItems
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RedemptionCodeListItem(
    modifier: Modifier,
    expandedItems: SnapshotStateList<HoYoLABGame>,
    item: Pair<HoYoLABGame, AnnotatedString>
) {
    val height by remember(expandedItems, item.first) {
        derivedStateOf {
            if (expandedItems.contains(item.first)) {
                Dp.Unspecified
            } else {
                216.dp
            }
        }
    }

    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .fillMaxWidth()
            .padding(horizontal = DefaultDp, vertical = HalfDp),
    ) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                navigationIcon = {
                    AsyncImage(
                        modifier = Modifier
                            .padding(12.dp)
                            .size(IconDp)
                            .clip(MaterialTheme.shapes.extraSmall),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.first.gameIconUrl)
                            .build(),
                        contentDescription = null
                    )
                },
                title = {
                    Text(text = stringResource(id = item.first.gameNameStringResId()))
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!expandedItems.contains(item.first)) {
                                expandedItems.add(item.first)
                            } else {
                                expandedItems.remove(item.first)
                            }
                        }
                    ) {
                        if (expandedItems.contains(item.first)) {
                            Icon(
                                imageVector = Icons.Default.ExpandLess,
                                contentDescription = Icons.Default.ExpandLess.name
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = Icons.Default.ExpandMore.name
                            )
                        }
                    }
                }
            )

            Row(
                modifier = Modifier
                    .height(height)
                    .padding(horizontal = DefaultDp),
            ) {
                SelectionContainer {
                    Text(text = item.second)
                }
            }
        }
    }
}

@Composable
private fun RedemptionCodeListItemPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DefaultDp, vertical = HalfDp),
    ) {
        Column(
            modifier = Modifier
                .padding(DefaultDp),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(IconDp)
                        .placeholder(
                            visible = true,
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    model = ImageRequest.Builder(LocalContext.current)
                        .build(),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.padding(DefaultDp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = true,
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    text = ""
                )
            }

            repeat(3) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .placeholder(
                            visible = true,
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    text = ""
                )
            }
        }
    }
}