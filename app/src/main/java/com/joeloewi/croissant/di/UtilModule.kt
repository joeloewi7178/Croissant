package com.joeloewi.croissant.di

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.core.content.getSystemService
import com.joeloewi.croissant.util.RootChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Singleton
    @Provides
    fun providePowerManager(application: Application): PowerManager =
        application.getSystemService()!!

    @Singleton
    @Provides
    fun provideAlarmManager(application: Application): AlarmManager =
        application.getSystemService()!!

    @Singleton
    @Provides
    fun provideRootChecker(application: Application): RootChecker = RootChecker(application)
}