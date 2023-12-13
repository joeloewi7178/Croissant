package com.joeloewi.croissant.data.repository.system.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.text.format.DateFormat
import com.joeloewi.croissant.data.repository.system.SystemDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SystemDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationHandler: Handler,
) : SystemDataSource {

    override fun is24HourFormat(): Flow<Boolean> = callbackFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(DateFormat.is24HourFormat(context))
            }
        }

        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)

        context.registerReceiver(broadcastReceiver, intentFilter, null, applicationHandler)

        awaitClose { context.unregisterReceiver(broadcastReceiver) }
    }.flowOn(Dispatchers.Default)
}