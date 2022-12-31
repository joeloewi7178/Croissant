package com.joeloewi.croissant.initializer

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors
import com.joeloewi.croissant.di.InitializerEntryPoint
import dagger.hilt.EntryPoints

class DynamicColorInitializer : Initializer<Unit> {

    private lateinit var application: Application

    override fun create(context: Context) {
        resolve(context)

        DynamicColors.applyToActivitiesIfAvailable(application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()

    private fun resolve(context: Context) {
        val initializerEntryPoint = EntryPoints.get(context, InitializerEntryPoint::class.java)

        application = initializerEntryPoint.application()
    }
}