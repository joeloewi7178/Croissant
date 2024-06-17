package com.joeloewi.croissant.core.network.di

import com.joeloewi.croissant.core.network.ArcaLiveAppDataSource
import com.joeloewi.croissant.core.network.ArcaLiveAppDataSourceImpl
import com.joeloewi.croissant.core.network.CheckInDataSource
import com.joeloewi.croissant.core.network.CheckInDataSourceImpl
import com.joeloewi.croissant.core.network.HoYoLABDataSource
import com.joeloewi.croissant.core.network.HoYoLABDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkDataSourceModule {

    @Binds
    abstract fun bindCheckInDataSource(checkInDataSourceImpl: CheckInDataSourceImpl): CheckInDataSource

    @Binds
    abstract fun bindHoYoLABDataSource(hoYoLABDataSourceImpl: HoYoLABDataSourceImpl): HoYoLABDataSource

    @Binds
    abstract fun bindArcaLiveAppDataSource(arcaLiveAppDataSourceImpl: ArcaLiveAppDataSourceImpl): ArcaLiveAppDataSource
}