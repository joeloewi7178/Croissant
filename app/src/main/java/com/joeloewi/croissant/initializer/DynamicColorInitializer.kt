package com.joeloewi.croissant.initializer

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.joeloewi.croissant.R

class DynamicColorInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        DynamicColors.applyToActivitiesIfAvailable(context.applicationContext as Application)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}