package com.joeloewi.croissant.util

import android.content.BroadcastReceiver
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun BroadcastReceiver.goAsync(
    onError: (cause: Throwable) -> Unit,
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit,
): Job {
    val coroutineScope = CoroutineScope(SupervisorJob() + coroutineContext)
    val pendingResult = goAsync()

    return coroutineScope.launch {
        try {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                onError(t)
            } finally {
                coroutineScope.cancel()
            }
        } finally {
            pendingResult.finish()
        }
    }
}