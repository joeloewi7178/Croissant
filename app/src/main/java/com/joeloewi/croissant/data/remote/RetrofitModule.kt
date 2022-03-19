package com.joeloewi.croissant.data.remote

import com.joeloewi.croissant.data.remote.model.response.GameRecordCardResponse
import com.joeloewi.croissant.data.remote.model.response.UserFullInfoResponse
import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(
        PolymorphicJsonAdapterFactory.of(
            BaseResponse::class.java,
            BaseResponse::class.java.name
        )
            .withSubtype(
                UserFullInfoResponse::class.java,
                UserFullInfoResponse::class.java.name
            )
            .withSubtype(
                GameRecordCardResponse::class.java,
                GameRecordCardResponse::class.java.name
            )
    ).build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
}