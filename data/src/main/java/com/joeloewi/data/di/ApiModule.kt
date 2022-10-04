package com.joeloewi.data.di

import com.joeloewi.data.api.dao.GenshinImpactCheckInService
import com.joeloewi.data.api.dao.HoYoLABService
import com.joeloewi.data.api.dao.HonkaiImpact3rdCheckInService
import com.joeloewi.data.api.dao.TearsOfThemisCheckInService
import com.joeloewi.data.api.model.response.*
import com.joeloewi.domain.entity.BaseResponse
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
    fun providePolymorphicJsonAdapterFactory(): PolymorphicJsonAdapterFactory<BaseResponse> =
        PolymorphicJsonAdapterFactory.of(
            BaseResponse::class.java,
            "type"
        ).withSubtype(
            UserFullInfoResponse::class.java,
            "userFullInfoResponse"
        ).withSubtype(
            GameRecordCardResponse::class.java,
            "gameRecordCardResponse"
        ).withSubtype(
            AttendanceResponse::class.java,
            "attendanceResponse"
        ).withSubtype(
            GenshinDailyNoteResponse::class.java,
            "genshinDailyNoteResponse"
        ).withSubtype(
            ChangeDataSwitchResponse::class.java,
            "changeDataSwitchResponse"
        )

    @Singleton
    @Provides
    fun provideMoshi(
        polymorphicJsonAdapterFactory: PolymorphicJsonAdapterFactory<BaseResponse>
    ): Moshi = Moshi.Builder()
        .add(polymorphicJsonAdapterFactory)
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .fastFallback(true)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit.Builder = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .validateEagerly(true)

    @Singleton
    @Provides
    fun provideHoYoLabService(retrofitBuilder: Retrofit.Builder): HoYoLABService =
        retrofitBuilder
            .baseUrl("https://bbs-api-os.hoyoverse.com/")
            .build()
            .create(HoYoLABService::class.java)

    @Singleton
    @Provides
    fun provideGenshinImpactCheckInService(retrofitBuilder: Retrofit.Builder): GenshinImpactCheckInService =
        retrofitBuilder
            .baseUrl("https://hk4e-api-os.mihoyo.com/")
            .build()
            .create(GenshinImpactCheckInService::class.java)

    @Singleton
    @Provides
    fun provideHonkaiImpact3rdCheckInService(retrofitBuilder: Retrofit.Builder): HonkaiImpact3rdCheckInService =
        retrofitBuilder
            .baseUrl("https://api-os-takumi.mihoyo.com/")
            .build()
            .create(HonkaiImpact3rdCheckInService::class.java)

    @Singleton
    @Provides
    fun provideTearsOfThemisCheckInService(retrofitBuilder: Retrofit.Builder): TearsOfThemisCheckInService =
        retrofitBuilder
            .baseUrl("https://sg-public-api.hoyolab.com/")
            .build()
            .create(TearsOfThemisCheckInService::class.java)
}