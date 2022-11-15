package com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen

import android.content.Intent
import android.graphics.Color
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.RedemptionCodesState
import com.joeloewi.croissant.state.rememberRedemptionCodesState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.HalfDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.util.rememberCssPrefersColorScheme
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel
import com.joeloewi.domain.common.HoYoLABGame
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun RedemptionCodesScreen(
    navController: NavController,
    redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()
) {
    val redemptionCodesState =
        rememberRedemptionCodesState(redemptionCodesViewModel = redemptionCodesViewModel)

    RedemptionCodesContent(
        redemptionCodesState = redemptionCodesState,
    )
}

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
        ) {
            with(redemptionCodesState.hoYoLABGameRedemptionCodesState) {
                when (this) {
                    is Lce.Content -> {
                        RedemptionCodes(
                            hoYoLABGameRedemptionCodes = content.toImmutableList(),
                            swipeRefreshState = redemptionCodesState.swipeRefreshState,
                            expandedItems = redemptionCodesState.expandedItems,
                            onRefresh = redemptionCodesState::onRefresh
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
        }
    }
}

@ExperimentalMaterial3Api
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

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun RedemptionCodes(
    hoYoLABGameRedemptionCodes: ImmutableList<Pair<HoYoLABGame, String>>,
    swipeRefreshState: SwipeRefreshState,
    expandedItems: SnapshotStateList<HoYoLABGame>,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeRefreshState,
        onRefresh = {
            expandedItems.clear()
            onRefresh()
        }
    ) {
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

@ExperimentalMaterial3Api
@Composable
private fun RedemptionCodeListItem(
    modifier: Modifier,
    expandedItems: SnapshotStateList<HoYoLABGame>,
    item: Pair<HoYoLABGame, String>
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
    val activity = LocalActivity.current
    val cssPrefersColorScheme = rememberCssPrefersColorScheme(
        contentColor = LocalContentColor.current
    )
    val webViewState = rememberWebViewStateWithHTMLData(
        data = cssPrefersColorScheme + item.second,
    )

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
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp
            )
        ) {
            Row(
                modifier = Modifier
                    .toggleable(
                        value = expandedItems.contains(item.first),
                        role = Role.Switch,
                        onValueChange = { checked ->
                            if (checked) {
                                expandedItems.add(item.first)
                            } else {
                                expandedItems.remove(item.first)
                            }
                        }
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(DefaultDp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(IconDp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.first.gameIconUrl)
                            .build(),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.padding(horizontal = DefaultDp))

                    Text(text = stringResource(id = item.first.gameNameStringResId()))
                }

                Box(modifier = Modifier.padding(DoubleDp)) {
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

            Row {
                WebView(
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth(),
                    state = webViewState,
                    onCreated = { webView ->
                        with(webView) {
                            runCatching {
                                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                                        settings,
                                        true
                                    )
                                }
                            }

                            settings.userAgentString =
                                "live.arca.android.playstore/0.8.331-playstore"

                            isVerticalScrollBarEnabled = false
                            isHorizontalScrollBarEnabled = false
                            setBackgroundColor(Color.TRANSPARENT)
                        }
                    },
                    client = remember {
                        object : AccompanistWebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                request?.url?.let {
                                    activity.startActivity(Intent(Intent.ACTION_VIEW, it))
                                }
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
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
                            color = MaterialTheme.colorScheme.outline,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        ),
                    text = ""
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                text = ""
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    ),
                text = ""
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = true,
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