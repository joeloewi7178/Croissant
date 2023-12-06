package com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers

@Composable
fun RedemptionCodesScreen(
    redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()
) {
    val hoYoLABGameRedemptionCodesState by redemptionCodesViewModel.hoYoLABGameRedemptionCodesState.collectAsStateWithLifecycle(
        context = Dispatchers.Default
    )
    val expandedItems = remember { SnapshotStateList<HoYoLABGame>() }

    RedemptionCodesContent(
        hoYoLABGameRedemptionCodesState = hoYoLABGameRedemptionCodesState,
        expandedItems = { expandedItems },
        onRefresh = { redemptionCodesViewModel.getRedemptionCodes() }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class
)
@Composable
private fun RedemptionCodesContent(
    hoYoLABGameRedemptionCodesState: Lce<List<Pair<HoYoLABGame, AnnotatedString>>>,
    expandedItems: () -> SnapshotStateList<HoYoLABGame>,
    onRefresh: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = hoYoLABGameRedemptionCodesState.isLoading,
        onRefresh = onRefresh
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.RedemptionCodes.resourceId))
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .pullRefresh(pullRefreshState)
        ) {
            with(hoYoLABGameRedemptionCodesState) {
                when (this) {
                    is Lce.Content -> {
                        RedemptionCodes(
                            hoYoLABGameRedemptionCodes = content.toImmutableList(),
                            expandedItems = expandedItems,
                        )
                    }

                    is Lce.Error -> {
                        RedemptionCodesError(onRefresh = onRefresh)
                    }

                    Lce.Loading -> {
                        RedemptionCodesLoading()
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = hoYoLABGameRedemptionCodesState.isLoading,
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
    expandedItems: () -> SnapshotStateList<HoYoLABGame>,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)
@Composable
private fun RedemptionCodeListItem(
    modifier: Modifier,
    expandedItems: () -> SnapshotStateList<HoYoLABGame>,
    item: Pair<HoYoLABGame, AnnotatedString>
) {
    val height by remember(expandedItems, item.first) {
        derivedStateOf {
            if (expandedItems().contains(item.first)) {
                Dp.Unspecified
            } else {
                216.dp
            }
        }
    }
    val context = LocalContext.current

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
                            if (!expandedItems().contains(item.first)) {
                                expandedItems().add(item.first)
                            } else {
                                expandedItems().remove(item.first)
                            }
                        }
                    ) {
                        if (expandedItems().contains(item.first)) {
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
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )

            Row(
                modifier = Modifier
                    .height(height)
                    .padding(horizontal = DefaultDp),
            ) {
                SelectionContainer {
                    val textStyle = LocalTextStyle.current
                    val textColor = textStyle.color.takeOrElse {
                        LocalContentColor.current
                    }

                    ClickableText(
                        text = item.second,
                        style = textStyle.copy(color = textColor),
                        onClick = { offset ->
                            item.second.getUrlAnnotations(offset, offset).firstOrNull()?.let {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(it.item.url)
                                    )
                                )
                            }
                        }
                    )
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