package com.joeloewi.croissant.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.joeloewi.croissant.receiver.TimeChangedReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

private val Context.is24HourFormatFlow: Flow<Boolean>
    get() = callbackFlow {
        val timeChangedReceiver = TimeChangedReceiver(
            onReceiveActionTimeChanged = {
                trySend(DateFormat.is24HourFormat(this@is24HourFormatFlow))
            }
        )
        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)

        registerReceiver(timeChangedReceiver, intentFilter)

        awaitClose {
            unregisterReceiver(timeChangedReceiver)
        }
    }

@ExperimentalLifecycleComposeApi
@Composable
fun Context.is24HourFormat(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): StateFlow<Boolean> = is24HourFormatFlow.stateIn(
    scope = coroutineScope,
    started = SharingStarted.Lazily,
    initialValue = DateFormat.is24HourFormat(this)
)