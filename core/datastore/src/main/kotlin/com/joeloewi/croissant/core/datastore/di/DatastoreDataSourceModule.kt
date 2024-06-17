package com.joeloewi.croissant.core.datastore.di

import com.joeloewi.croissant.core.datastore.SettingsDataSource
import com.joeloewi.croissant.core.datastore.SettingsDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreDataSourceModule {

    @Binds
    abstract fun bindSettingsDataSource(settingsDataSourceImpl: SettingsDataSourceImpl): SettingsDataSource
}