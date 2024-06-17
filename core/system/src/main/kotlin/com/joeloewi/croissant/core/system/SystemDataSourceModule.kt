package com.joeloewi.croissant.core.system

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemDataSourceModule {

    @Binds
    abstract fun bindSystemDataSource(systemDataSourceImpl: SystemDataSourceImpl): SystemDataSource
}
