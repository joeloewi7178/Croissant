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

package com.joeloewi.croissant.data.api.dao

import com.joeloewi.croissant.data.api.model.request.DataSwitchRequest
import com.joeloewi.croissant.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.croissant.data.api.model.response.GameRecordCardResponse
import com.joeloewi.croissant.data.api.model.response.GenshinDailyNoteResponse
import com.joeloewi.croissant.data.api.model.response.UserFullInfoResponse
import com.joeloewi.croissant.data.common.HeaderInformation
import com.joeloewi.croissant.data.common.generateDS
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.http.*
import java.time.Instant

interface HoYoLABService {

    @GET("community/painter/wapi/user/full")
    fun getUserFullInfo(
        @Header("Cookie") cookie: String,
        @Query("t") currentMillis: Long = Instant.now().toEpochMilli()
    ): Call<ApiResponse<UserFullInfoResponse>>

    @GET("game_record/card/wapi/getGameRecordCard")
    fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): Call<ApiResponse<GameRecordCardResponse>>

    @POST("game_record/card/wapi/changeDataSwitch")
    fun changeDataSwitch(
        @Header("Cookie") cookie: String,
        @Body dataSwitchRequest: DataSwitchRequest
    ): Call<ApiResponse<ChangeDataSwitchResponse>>

    @GET("game_record/genshin/api/dailyNote")
    fun getGenshinDailyNote(
        @Header("DS") ds: String = generateDS(headerInformation = HeaderInformation.OS),
        @Header("Cookie") cookie: String,
        @Header("x-rpc-app_version") xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        @Header("x-rpc-client_type") xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        @Query("role_id") roleId: Long,
        @Query("server") server: String,
    ): Call<ApiResponse<GenshinDailyNoteResponse>>
}