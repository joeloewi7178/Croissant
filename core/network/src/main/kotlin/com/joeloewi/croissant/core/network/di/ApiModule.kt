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

package com.joeloewi.croissant.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.joeloewi.croissant.core.common.di.IoDispatcherExecutor
import com.joeloewi.croissant.core.network.BuildConfig
import com.joeloewi.croissant.core.network.dao.ArcaLiveAppService
import com.joeloewi.croissant.core.network.dao.CheckInService
import com.joeloewi.croissant.core.network.dao.GenshinImpactCheckInService
import com.joeloewi.croissant.core.network.dao.HoYoLABService
import com.joeloewi.croissant.core.network.dao.ZenlessZoneZeroCheckInService
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
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
                return@run addInterceptor {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                        .intercept(it)
                }
            }
            return@run this
        }
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        @IoDispatcherExecutor executor: Executor,
        okHttpClient: dagger.Lazy<OkHttpClient>
    ): Retrofit.Builder = Retrofit.Builder()
        .callFactory { okHttpClient.get().newCall(it) }
        .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
        .callbackExecutor { executor.execute(it) }
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .validateEagerly(true)

    @Singleton
    @Provides
    fun provideHoYoLabService(
        retrofitBuilder: Retrofit.Builder
    ): HoYoLABService = retrofitBuilder
        .baseUrl("https://bbs-api-os.hoyolab.com/")
        .build()
        .create()

    @Singleton
    @Provides
    fun provideGenshinImpactCheckInService(
        retrofitBuilder: Retrofit.Builder
    ): GenshinImpactCheckInService = retrofitBuilder
        .baseUrl("https://sg-hk4e-api.hoyolab.com/")
        .build()
        .create()

    @Singleton
    @Provides
    fun provideCommonCheckInService(
        retrofitBuilder: Retrofit.Builder
    ): CheckInService = retrofitBuilder
        .baseUrl("https://sg-public-api.hoyolab.com/")
        .build()
        .create()

    @Singleton
    @Provides
    fun provideArcaLiveAppService(
        retrofitBuilder: Retrofit.Builder
    ): ArcaLiveAppService = retrofitBuilder
        .baseUrl("https://arca.live/api/app/")
        .build()
        .create()

    @Singleton
    @Provides
    fun providesZenlessZoneZeroCheckInService(
        retrofitBuilder: Retrofit.Builder
    ): ZenlessZoneZeroCheckInService = retrofitBuilder
        .baseUrl("https://sg-act-nap-api.hoyolab.com")
        .build()
        .create()
}