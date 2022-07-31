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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

private fun Context.is24HourFormatFlow(
    coroutineScope: CoroutineScope
): StateFlow<Boolean> =
    callbackFlow {
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
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = DateFormat.is24HourFormat(this)
    )

@ExperimentalLifecycleComposeApi
@Composable
fun Context.is24HourFormat(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): StateFlow<Boolean> = is24HourFormatFlow(coroutineScope)