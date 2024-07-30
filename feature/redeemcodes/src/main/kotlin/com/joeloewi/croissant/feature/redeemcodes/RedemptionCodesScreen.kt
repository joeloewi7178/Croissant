package com.joeloewi.croissant.feature.redeemcodes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.core.designsystem.component.RedemptionCodeListItemPlaceholder
import com.joeloewi.croissant.core.ui.CroissantNavigation
import com.joeloewi.croissant.core.ui.LCE
import com.joeloewi.croissant.core.ui.LocalActivity
import com.joeloewi.croissant.core.ui.RedemptionCodeListItem
import com.joeloewi.croissant.core.ui.RedemptionCodesError
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedemptionCodesScreen(
    redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current
    val state by redemptionCodesViewModel.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    redemptionCodesViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is RedemptionCodesViewModel.SideEffect.LaunchIntent -> {
                activity.startActivity(sideEffect.intent)
            }

            RedemptionCodesViewModel.SideEffect.EndRefresh -> {
                pullToRefreshState.endRefresh()
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { pullToRefreshState.isRefreshing }.catch { }
            .distinctUntilChanged()
            .collect { isRefreshing ->
                if (isRefreshing) {
                    redemptionCodesViewModel.onStartRefresh()
                }
            }
    }

    LaunchedEffect(state.redemptionCodes) {
        when (state.redemptionCodes) {
            LCE.Loading -> {

            }

            else -> {
                if (pullToRefreshState.isRefreshing) {
                    pullToRefreshState.endRefresh()
                }
            }
        }
    }

    RedemptionCodesContent(
        state = state,
        pullToRefreshState = pullToRefreshState,
        onRefresh = redemptionCodesViewModel::fetchRedemptionCodes,
        onClickUrl = redemptionCodesViewModel::onClickUrl,
        onClickExpand = redemptionCodesViewModel::onClickExpand
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun RedemptionCodesContent(
    state: RedemptionCodesViewModel.State,
    pullToRefreshState: PullToRefreshState,
    onRefresh: () -> Unit,
    onClickUrl: (annotatedString: AnnotatedString, offset: Int) -> Unit,
    onClickExpand: (hoYoLABGame: HoYoLABGame) -> Unit
) {
    Scaffold(
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
                .fillMaxSize()
                .padding(innerPadding)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                when (val redemptionCodesState = state.redemptionCodes) {
                    is LCE.Content -> {
                        items(
                            items = redemptionCodesState.content,
                            key = { it.first.name },
                            contentType = { it::class.java.simpleName }
                        ) { item ->
                            RedemptionCodeListItem(
                                modifier = Modifier.animateItemPlacement(),
                                iconUrl = item.first.gameIconUrl,
                                gameName = item.first.name,
                                htmlString = item.second,
                                isExpanded = item.first in state.expandedItems,
                                onClickUrl = onClickUrl,
                                onClickExpand = { onClickExpand(item.first) }
                            )
                        }
                    }

                    is LCE.Error -> {
                        item(
                            key = "redemptionCodesError",
                            contentType = "RedemptionCodesError"
                        ) {
                            RedemptionCodesError(
                                modifier = Modifier.fillParentMaxSize(),
                                onRefresh = onRefresh,
                                dueToSitePolicyText = stringResource(id = R.string.feature_redeemcodes_due_to_site_policy),
                                errorOccurredText = stringResource(id = R.string.feature_redeemcodes_error_occurred),
                                retryText = stringResource(id = R.string.feature_redeemcodes_retry)
                            )
                        }
                    }

                    LCE.Loading -> {
                        items(
                            items = IntArray(3) { it }.toTypedArray(),
                            key = { "placeholder${it}" },
                            contentType = { "Placeholder" }
                        ) {
                            RedemptionCodeListItemPlaceholder()
                        }
                    }
                }
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState
            )
        }
    }
}