package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors
import com.joeloewi.croissant.di.InitializerEntryPoint
import com.joeloewi.croissant.di.entryPoints

class DynamicColorInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val initializerEntryPoint: InitializerEntryPoint by context.entryPoints()
        val application = initializerEntryPoint.application()

        DynamicColors.applyToActivitiesIfAvailable(application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}