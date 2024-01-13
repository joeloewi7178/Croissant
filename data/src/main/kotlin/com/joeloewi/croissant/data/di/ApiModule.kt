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

import com.joeloewi.croissant.data.BuildConfig
import com.joeloewi.croissant.data.api.dao.*
import com.joeloewi.croissant.data.api.model.response.*
import com.joeloewi.croissant.domain.entity.BaseResponse
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
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
import retrofit2.create
import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .proxySelector(object : ProxySelector() {
            override fun select(p0: URI?): MutableList<Proxy> = runCatching {
                getDefault().select(p0)
            }.getOrNull() ?: mutableListOf(Proxy.NO_PROXY)

            override fun connectFailed(p0: URI?, p1: SocketAddress?, p2: IOException?) {
                getDefault().connectFailed(p0, p1, p2)
            }
        })
        .run {
            if (BuildConfig.DEBUG) {
                return@run addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
            return@run this
        }
        .retryOnConnectionFailure(true)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        @IoDispatcherExecutor executor: Executor,
        okHttpClient: OkHttpClient
    ): Retrofit.Builder = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
        .callbackExecutor(executor)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(
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
                    )
                    .build()
            )
        )
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
            .baseUrl("https://sg-hk4e-api.hoyolab.com/")
            .build()
            .create()

    @Singleton
    @Provides
    fun provideCommonCheckInService(retrofitBuilder: Retrofit.Builder): CheckInService =
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