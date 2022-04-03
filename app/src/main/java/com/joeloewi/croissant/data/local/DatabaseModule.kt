package com.joeloewi.croissant.data.local

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCroissantDatabase(application: Application): CroissantDatabase = Room.databaseBuilder(
        application,
        CroissantDatabase::class.java,
        "croissant"
    ).build()
}