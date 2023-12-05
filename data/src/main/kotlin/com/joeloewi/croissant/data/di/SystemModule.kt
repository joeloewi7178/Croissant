package com.joeloewi.croissant.data.di

import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import androidx.core.os.HandlerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {
    @Singleton
    @Provides
    fun provideApplicationHandler(): Handler =
        HandlerThread("ApplicationHandler", Process.THREAD_PRIORITY_DEFAULT).apply {
            isDaemon = true
            start()
        }.let {
            HandlerCompat.createAsync(it.looper)
        }
}