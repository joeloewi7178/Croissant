package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class ResinStatusWidgetDetailState(
    private val resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel
) {
    val selectableIntervals = resinStatusWidgetDetailViewModel.selectableIntervals.toImmutableList()
    val interval
        @Composable get() = resinStatusWidgetDetailViewModel.interval.collectAsStateWithLifecycle().value
    val updateResinStatusWidgetState
        @Composable get() = resinStatusWidgetDetailViewModel.updateResinStatusWidgetState.collectAsStateWithLifecycle().value
    val showProgressDialog
        @Composable get() = updateResinStatusWidgetState.isLoading

    fun onIntervalChange(interval: Long) {
        resinStatusWidgetDetailViewModel.setInterval(interval)
    }

    fun updateResinStatusWidget() {
        resinStatusWidgetDetailViewModel.updateResinStatusWidget()
    }
}

@Composable
fun rememberResinStatusWidgetDetailState(
    resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel
) = remember {
    ResinStatusWidgetDetailState(
        resinStatusWidgetDetailViewModel = resinStatusWidgetDetailViewModel
    )
}
