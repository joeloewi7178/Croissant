package com.joeloewi.croissant.data.di

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.HandlerThread
import android.os.PowerManager
import android.os.Process
import androidx.core.content.getSystemService
import androidx.core.os.HandlerCompat
import com.joeloewi.croissant.data.system.RootChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {
    @Singleton
    @Provides
    fun provideApplicationHandler(): Handler =
        HandlerThread("ApplicationHandlerThread", Process.THREAD_PRIORITY_DEFAULT).apply {
            isDaemon = true
            start()
        }.let {
            HandlerCompat.createAsync(it.looper)
        }

    @Provides
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager = context.getSystemService()!!

    @Provides
    fun providePowerManager(
        @ApplicationContext context: Context
    ): PowerManager = context.getSystemService()!!

    @Singleton
    @Provides
    fun provideRootChecker(
        @ApplicationContext context: Context
    ): RootChecker = RootChecker(context)

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager = context.getSystemService()!!

    @Provides
    fun provideAppWidgetManager(
        @ApplicationContext context: Context
    ): AppWidgetManager = context.getSystemService()!!
}