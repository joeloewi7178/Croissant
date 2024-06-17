package com.joeloewi.croissant.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.joeloewi.croissant.core.datastore.Settings
import com.joeloewi.croissant.core.datastore.SettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    internal fun provideSettingsSerializer(): SettingsSerializer = SettingsSerializer()

    @Singleton
    @Provides
    internal fun provideSettingsDataStore(
        @ApplicationContext context: Context,
        settingsSerializer: SettingsSerializer
    ): DataStore<Settings> = DataStoreFactory.create(
        serializer = settingsSerializer,
        corruptionHandler = ReplaceFileCorruptionHandler {
            Settings.getDefaultInstance()
        }
    ) {
        context.dataStoreFile("settings.pb")
    }
}