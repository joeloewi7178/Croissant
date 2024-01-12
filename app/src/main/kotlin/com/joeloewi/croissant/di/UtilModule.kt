package com.joeloewi.croissant.di

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import androidx.work.RunnableScheduler
import androidx.work.WorkManager
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.AlarmScheduler
import com.joeloewi.croissant.util.NotificationGenerator
import com.joeloewi.croissant.util.TextToSpeechFactory
import com.joeloewi.croissant.util.impl.AlarmSchedulerImpl
import com.joeloewi.croissant.util.impl.RunnableSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    ): TextToSpeechFactory = TextToSpeechFactory(context)

    @Singleton
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
        .components {
            add(SvgDecoder.Factory())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .run {
            if (BuildConfig.DEBUG) {
                logger(DebugLogger())
            } else {
                this
            }
        }
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