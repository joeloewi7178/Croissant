package com.joeloewi.croissant.state

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.joeloewi.croissant.viewmodel.RedemptionCodesViewModel

@ExperimentalLifecycleComposeApi
@Stable
class RedemptionCodesState(
    val snackbarHostState: SnackbarHostState,
    val redemptionCodesViewModel: RedemptionCodesViewModel
) {
    //state
    val hoYoLABGameRedemptionCodesState
        @Composable get() = redemptionCodesViewModel.hoYoLABGameRedemptionCodesState.collectAsStateWithLifecycle().value
    val swipeRefreshState
        @Composable get() = rememberSwipeRefreshState(isRefreshing = hoYoLABGameRedemptionCodesState.isLoading)

    //state list
    val expandedItems
        get() = redemptionCodesViewModel.expandedItems

    //function
    fun onRefresh() {
        redemptionCodesViewModel.getRedemptionCodes()
    }
}

@ExperimentalLifecycleComposeApi
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