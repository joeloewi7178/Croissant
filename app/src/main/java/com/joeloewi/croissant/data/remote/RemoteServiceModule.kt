package com.joeloewi.croissant.data.remote

import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteServiceModule {

    @Singleton
    @Provides
    fun provideHoYoLabService(retrofitBuilder: Retrofit.Builder): HoYoLABService =
        retrofitBuilder
            .baseUrl("https://bbs-api-os.hoyolab.com/")
            .build()
            .create(HoYoLABService::class.java)
}