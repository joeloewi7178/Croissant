package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import com.joeloewi.croissant.util.createNotificationChannels

class NotificationChannelInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        context.createNotificationChannels()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}