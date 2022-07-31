package com.joeloewi.croissant.di

import android.app.AlarmManager
import android.app.Application
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.joeloewi.croissant.util.RootChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Provides
    @Singleton
    fun providePowerManager(application: Application): PowerManager =
        ContextCompat.getSystemService(application, PowerManager::class.java)!!

    @Provides
    @Singleton
    fun provideAlarmManager(application: Application): AlarmManager =
        ContextCompat.getSystemService(application, AlarmManager::class.java)!!

    @Provides
    @Singleton
    fun provideRootChecker(application: Application): RootChecker = RootChecker(application)
}