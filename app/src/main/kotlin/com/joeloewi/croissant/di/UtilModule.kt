package com.joeloewi.croissant.di

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import coil.ImageLoader
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.RootChecker
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
    fun providePowerManager(application: Application): PowerManager =
        application.getSystemService()!!

    @Provides
    fun provideAlarmManager(application: Application): AlarmManager =
        application.getSystemService()!!

    @Provides
    fun provideRootChecker(application: Application): RootChecker = RootChecker(application)

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