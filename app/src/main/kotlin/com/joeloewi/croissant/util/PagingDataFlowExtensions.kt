package com.joeloewi.croissant.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItemsWithLifecycle(
    context: CoroutineContext = EmptyCoroutineContext,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
) = remember(this, lifecycle) {
    flowWithLifecycle(lifecycle, minActiveState)
}.collectAsLazyPagingItems(context)