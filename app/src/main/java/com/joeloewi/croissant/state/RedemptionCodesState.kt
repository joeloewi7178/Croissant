package com.joeloewi.croissant.state

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class RedemptionCodesState(
    val snackbarHostState: SnackbarHostState,
    val redemptionCodesViewModel: RedemptionCodesViewModel
) {
    //state
    val hoYoLABGameRedemptionCodesState
        @Composable get() = redemptionCodesViewModel.hoYoLABGameRedemptionCodesState.collectAsStateWithLifecycle().value

    @OptIn(ExperimentalMaterialApi::class)
    val swipeRefreshState
        @Composable get() = rememberPullRefreshState(
            refreshing = hoYoLABGameRedemptionCodesState.isLoading,
            onRefresh = redemptionCodesViewModel::getRedemptionCodes
        )

    //state list
    val expandedItems
        get() = redemptionCodesViewModel.expandedItems

    //function
    fun onRefresh() {
        redemptionCodesViewModel.getRedemptionCodes()
    }
}

@Composable
fun rememberRedemptionCodesState(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    redemptionCodesViewModel: RedemptionCodesViewModel
) = remember(
    snackbarHostState,
    redemptionCodesViewModel
) {
    RedemptionCodesState(
        snackbarHostState = snackbarHostState,
        redemptionCodesViewModel = redemptionCodesViewModel
    )
}