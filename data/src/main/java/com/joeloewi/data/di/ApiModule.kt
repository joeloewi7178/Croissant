package com.joeloewi.data.di

import com.joeloewi.data.api.dao.HoYoLABService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.data.api.model.response.GameRecordCardResponse
import com.joeloewi.data.api.model.response.GenshinDailyNoteResponse
import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.UserFullInfoResponse
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(
        PolymorphicJsonAdapterFactory.of(
            BaseResponse::class.java,
            BaseResponse::class.java.name
        )
            .withSubtype(
                UserFullInfoResponse::class.java,
                UserFullInfoResponse::class.java.simpleName
            )
            .withSubtype(
                GameRecordCardResponse::class.java,
                GameRecordCardResponse::class.java.simpleName
            )
            .withSubtype(
                AttendanceResponse::class.java,
                AttendanceResponse::class.java.simpleName
            )
            .withSubtype(
                GenshinDailyNoteResponse::class.java,
                GenshinDailyNoteResponse::class.java.simpleName
            )
            .withSubtype(
                ChangeDataSwitchResponse::class.java,
                ChangeDataSwitchResponse::class.java.simpleName
            )
    ).build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit.Builder =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))

    @Singleton
    @Provides
    fun provideHoYoLabService(retrofitBuilder: Retrofit.Builder): HoYoLABService =
        retrofitBuilder
            .baseUrl("https://bbs-api-os.hoyoverse.com/")
            .build()
            .create(HoYoLABService::class.java)
}