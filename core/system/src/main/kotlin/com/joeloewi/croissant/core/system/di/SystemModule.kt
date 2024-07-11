package com.joeloewi.croissant.core.system.di

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.HandlerThread
import android.os.PowerManager
import android.os.Process
import androidx.core.content.getSystemService
import com.joeloewi.croissant.core.system.PermissionChecker
import com.joeloewi.croissant.core.system.RootChecker
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
        }.let { Handler(it.looper) }

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
    fun provideAppWidgetManager(
        @ApplicationContext context: Context
    ): AppWidgetManager = context.getSystemService()!!

    @Provides
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager = context.packageManager

    @Provides
    fun providePermissionChecker(
        @ApplicationContext context: Context
    ): PermissionChecker = PermissionChecker(context)
}