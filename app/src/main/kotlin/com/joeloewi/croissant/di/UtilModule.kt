package com.joeloewi.croissant.di

import android.content.Context
import android.text.format.DateFormat
import androidx.work.RunnableScheduler
import androidx.work.WorkManager
import coil.ImageLoader
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.util.NotificationGenerator
import com.joeloewi.croissant.util.impl.AlarmSchedulerImpl
import com.joeloewi.croissant.util.impl.RunnableSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.TextToSpeechFactory
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    fun provideIs24HourFormat(
        @ApplicationContext context: Context
    ): Boolean = DateFormat.is24HourFormat(context)

    @Provides
    fun provideAppUpdateManager(
        @ApplicationContext context: Context
    ): AppUpdateManager = AppUpdateManagerFactory.create(context)

    @Provides
    fun provideTextToSpeechFactory(
        @ApplicationContext context: Context
    ): TextToSpeechFactory = TextToSpeechFactory(context, TextToSpeechEngine.SystemDefault)

    @Provides
    fun provideNotificationGenerator(
        @ApplicationContext context: Context
    ): NotificationGenerator = NotificationGenerator(context)

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader = ImageLoader.Builder(context)
        .crossfade(true)
        .okHttpClient(okHttpClient)
        .placeholder(R.drawable.image_placeholder)
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilModuleForBind {
    @Singleton
    @Binds
    abstract fun bindRunnableScheduler(
        runnableSchedulerImpl: RunnableSchedulerImpl
    ): RunnableScheduler

    @Singleton
    @Binds
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): AlarmScheduler
}