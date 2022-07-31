package com.joeloewi.croissant.initializer

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors

class DynamicColorInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        DynamicColors.applyToActivitiesIfAvailable(context.applicationContext as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}