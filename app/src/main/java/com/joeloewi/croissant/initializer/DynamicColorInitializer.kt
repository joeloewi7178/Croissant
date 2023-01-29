package com.joeloewi.croissant.initializer

import android.content.Context
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors
import com.joeloewi.croissant.initializer.base.HiltInitializer

class DynamicColorInitializer : HiltInitializer<Unit> {
    override fun createWithEntryPoint(
        context: Context,
        initializerEntryPoint: HiltInitializer.InitializerEntryPoint
    ) {
        val application = initializerEntryPoint.application()

        DynamicColors.applyToActivitiesIfAvailable(application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}