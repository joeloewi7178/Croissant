package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import com.joeloewi.croissant.di.InitializerEntryPoint
import com.joeloewi.croissant.util.NotificationGenerator
import javax.inject.Inject

class NotificationChannelInitializer : Initializer<Unit> {

    @set:Inject
    internal lateinit var notificationGenerator: NotificationGenerator

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).injectNotificationChannelInitializer(this)

        notificationGenerator.createNotificationChannels()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}