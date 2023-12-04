package com.joeloewi.croissant.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.DateFormat
import com.joeloewi.croissant.receiver.TimeChangedReceiver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

val Context.is24HourFormat: Flow<HourFormat>
    get() = callbackFlow {
        val timeChangedReceiver = TimeChangedReceiver(
            onReceiveActionTimeChanged = {
                trySend(HourFormat.fromSystemHourFormat(DateFormat.is24HourFormat(this@is24HourFormat)))
            }
        )
        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)

        registerReceiver(timeChangedReceiver, intentFilter)

        awaitClose {
            unregisterReceiver(timeChangedReceiver)
        }
    }