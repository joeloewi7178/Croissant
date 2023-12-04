package com.joeloewi.croissant.initializer

import android.content.Context
import android.content.pm.PackageManager
import androidx.startup.Initializer
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.createNotificationChannels

class NotificationChannelInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (context.packageManager.checkPermission(
                CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT,
                context.packageName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.createNotificationChannels()
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}