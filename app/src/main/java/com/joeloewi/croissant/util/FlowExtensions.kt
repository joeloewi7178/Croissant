package com.joeloewi.croissant.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@Composable
internal fun <T> rememberFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) { flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED) }
}

@Composable
internal fun <T> rememberFlow(
    flow: StateFlow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    started: SharingStarted = SharingStarted.WhileSubscribed()
): StateFlow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) {
        flow.stateIn(
            scope = lifecycleOwner.lifecycleScope,
            started = started,
            initialValue = flow.value
        )
    }
}

@Composable
fun <T : R, R> Flow<T>.collectAsStateLifecycleAware(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> = rememberFlow(flow = this).collectAsState(initial = initial, context = context)

@Composable
fun <T> StateFlow<T>.collectAsStateLifecycleAware(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    started: SharingStarted = SharingStarted.WhileSubscribed(),
    context: CoroutineContext = EmptyCoroutineContext
): State<T> =
    rememberFlow(lifecycleOwner = lifecycleOwner, started = started, flow = this).collectAsState(
        context = context
    )
