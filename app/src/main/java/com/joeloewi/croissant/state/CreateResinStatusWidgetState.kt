package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class CreateResinStatusWidgetState(
    private val createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val selectableIntervals = createResinStatusWidgetViewModel.selectableIntervals.toImmutableList()
    val interval
        @Composable get() = createResinStatusWidgetViewModel.interval.collectAsStateWithLifecycle().value
    val pagedAttendancesWithGames
        @Composable get() = createResinStatusWidgetViewModel.pagedAttendancesWithGames.collectAsLazyPagingItems(
            Dispatchers.IO
        )
    val checkedAttendanceIds = createResinStatusWidgetViewModel.checkedAttendanceIds
    val insertResinStatusWidgetState
        @Composable get() = createResinStatusWidgetViewModel.createResinStatusWidgetState.collectAsStateWithLifecycle().value
    val appWidgetId
        get() = createResinStatusWidgetViewModel.appWidgetId
    val isAttendanceIdItemSelected
        get() = checkedAttendanceIds.isNotEmpty()
    val showProgressDialog
        @Composable get() = insertResinStatusWidgetState.isLoading

    fun onClickDone() {
        createResinStatusWidgetViewModel.configureAppWidget()
    }

    fun onIntervalChange(interval: Long) {
        createResinStatusWidgetViewModel.setInterval(interval)
    }
}

@Composable
fun rememberCreateResinStatusWidgetState(
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) = remember(
    createResinStatusWidgetViewModel
) {
    CreateResinStatusWidgetState(
        createResinStatusWidgetViewModel = createResinStatusWidgetViewModel
    )
}