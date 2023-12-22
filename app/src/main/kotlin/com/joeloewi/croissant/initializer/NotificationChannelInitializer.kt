package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import com.joeloewi.croissant.di.InitializerEntryPoint
import com.joeloewi.croissant.di.entryPoints

class NotificationChannelInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val initializerEntryPoint: InitializerEntryPoint by context.entryPoints()
        val notificationGenerator = initializerEntryPoint.notificationGenerator()

        notificationGenerator.createNotificationChannels()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}