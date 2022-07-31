package com.joeloewi.croissant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeChangedReceiver(
    private val onReceiveActionTimeChanged: () -> Unit
) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        onReceiveActionTimeChanged()
    }
}