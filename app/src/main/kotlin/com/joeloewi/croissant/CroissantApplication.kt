package com.joeloewi.croissant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CroissantApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}