/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.data.di

import com.joeloewi.croissant.data.api.dao.*
import com.joeloewi.croissant.data.api.model.response.*
import com.joeloewi.croissant.domain.entity.BaseResponse
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
import retrofit2.create
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
            .baseUrl("https://bbs-api-os.hoyolab.com/")
            .build()
            .create()

    @Singleton
    @Provides
    fun provideGenshinImpactCheckInService(retrofitBuilder: Retrofit.Builder): GenshinImpactCheckInService =
        retrofitBuilder
            .baseUrl("https://hk4e-api-os.mihoyo.com/")
            .build()
            .create()

    @Singleton
    @Provides
    fun provideHonkaiImpact3rdCheckInService(retrofitBuilder: Retrofit.Builder): HonkaiImpact3rdCheckInService =
        retrofitBuilder
            .baseUrl("https://api-os-takumi.mihoyo.com/")
            .build()
            .create()

    @Singleton
    @Provides
    fun provideTearsOfThemisCheckInService(retrofitBuilder: Retrofit.Builder): TearsOfThemisCheckInService =
        retrofitBuilder
            .baseUrl("https://sg-public-api.hoyolab.com/")
            .build()
            .create()

    @Singleton
    @Provides
    fun provideArcaLiveAppService(retrofitBuilder: Retrofit.Builder): ArcaLiveAppService =
        retrofitBuilder
            .baseUrl("https://arca.live/api/app/")
            .build()
            .create()
}