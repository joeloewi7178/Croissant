package com.joeloewi.croissant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.joeloewi.croissant.util.NotificationGenerator
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TimeZoneChangedReceiver @Inject constructor(
) : BroadcastReceiver() {

    @Inject
    lateinit var notificationGenerator: NotificationGenerator

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_TIMEZONE_CHANGED -> {
                with(notificationGenerator) {
                    safeNotify(
                        UUID.randomUUID().toString(),
                        0,
                        createTimezoneChangedNotification()
                    )
                }
            }

            else -> {

            }
        }
    }
}