package com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen

import android.content.Intent
import android.graphics.Color
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.ui.theme.IconDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.gameNameStringResId
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel
import com.joeloewi.domain.common.HoYoLABGame

@ExperimentalLifecycleComposeApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun RedemptionCodesScreen(
    navController: NavController,
    redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()
) {
    val hoYoLABGameRedemptionCodesState by redemptionCodesViewModel.hoYoLABGameRedemptionCodesState.collectAsStateWithLifecycle()
    val expandedItems = remember { redemptionCodesViewModel.expandedItems }

    RedemptionCodesContent(
        hoYoLABGameRedemptionCodesState = hoYoLABGameRedemptionCodesState,
        expandedItems = expandedItems,
        onRefresh = redemptionCodesViewModel::getRedemptionCodes
    )
}

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
private fun RedemptionCodesContent(
    hoYoLABGameRedemptionCodesState: Lce<List<Pair<HoYoLABGame, String>>>,
    expandedItems: SnapshotStateList<HoYoLABGame>,
    onRefresh: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.RedemptionCodes.resourceId))
                }
            )
        }
    ) { innerPadding ->
        val swipeRefreshState = rememberSwipeRefreshState(hoYoLABGameRedemptionCodesState.isLoading)
        val errorOccurredString = stringResource(id = R.string.error_occurred)

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
                    .fillMaxSize()
                    .padding(innerPadding)
                    .then(Modifier.padding(horizontal = DefaultDp)),
                verticalArrangement = Arrangement.spacedBy(
                    space = DefaultDp
                )
            ) {
                when (hoYoLABGameRedemptionCodesState) {
                    is Lce.Content -> {
                        items(
                            items = hoYoLABGameRedemptionCodesState.content,
                            key = { it.first.gameId }
                        ) { item ->
                            when (item.first) {
                                HoYoLABGame.Unknown -> {

                                }

                                HoYoLABGame.TearsOfThemis -> {

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
                    is Lce.Error -> {
                        item {
                            LaunchedEffect(hoYoLABGameRedemptionCodesState) {
                                snackbarHostState.showSnackbar(
                                    message = errorOccurredString,
                                )
                            }
                        }
                    }
                    Lce.Loading -> {
                        items(
                            items = IntArray(3) { it }.toTypedArray(),
                            key = { "placeholder${it}" }
                        ) {
                            RedemptionCodeListItemPlaceholder()
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RedemptionCodeListItem(
    modifier: Modifier,
    expandedItems: SnapshotStateList<HoYoLABGame>,
    item: Pair<HoYoLABGame, String>
) {
    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp
            )
        ) {
            val darkTheme = isSystemInDarkTheme()
            val activity = LocalActivity.current

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

            val height by remember(expandedItems, item.first) {
                derivedStateOf {
                    if (expandedItems.contains(item.first)) {
                        Dp.Unspecified
                    } else {
                        216.dp
                    }
                }
            }

            WebView(
                modifier = Modifier
                    .height(height)
                    .fillMaxWidth(),
                state = rememberWebViewStateWithHTMLData(
                    data = item.second,
                    baseUrl = "https://arca.live/"
                ),
                onCreated = { webView ->
                    with(webView) {
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && darkTheme) {
                            WebSettingsCompat.setForceDark(
                                settings,
                                WebSettingsCompat.FORCE_DARK_ON
                            )
                        }
                        isVerticalScrollBarEnabled = false
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
                            return true
                        }
                    }
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun RedemptionCodeListItemPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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