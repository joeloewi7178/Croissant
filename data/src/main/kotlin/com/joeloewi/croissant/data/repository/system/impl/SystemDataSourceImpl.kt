package com.joeloewi.croissant.data.repository.system.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.HandlerThread
import android.text.format.DateFormat
import androidx.core.os.HandlerCompat
import com.joeloewi.croissant.data.repository.system.SystemDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SystemDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SystemDataSource {

    override fun is24HourFormat(): Flow<Boolean> = callbackFlow {
        val handlerThread = HandlerThread("HourFormatBroadcastReceiver").apply {
            start()
        }
        val handler = HandlerCompat.createAsync(handlerThread.looper)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(DateFormat.is24HourFormat(context))
            }
        }

        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)

        context.registerReceiver(broadcastReceiver, intentFilter, null, handler)

        awaitClose {
            context.unregisterReceiver(broadcastReceiver)
            handlerThread.quit()
        }
    }.flowOn(Dispatchers.Default)
}