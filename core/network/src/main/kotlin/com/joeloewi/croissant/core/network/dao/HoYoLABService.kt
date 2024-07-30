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

package com.joeloewi.croissant.core.network.dao

import com.joeloewi.croissant.core.network.HeaderInformation
import com.joeloewi.croissant.core.network.generateDS
import com.joeloewi.croissant.core.network.model.request.DataSwitchRequest
import com.joeloewi.croissant.core.network.model.response.HoYoLABResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HoYoLABService {

    @GET("community/painter/wapi/user/full")
    suspend fun getUserFullInfo(
        @Header("Cookie") cookie: String
    ): ApiResponse<HoYoLABResponse.UserFullInfoResponse>

    @GET("game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): ApiResponse<HoYoLABResponse.GameRecordCardResponse>

    @POST("game_record/card/wapi/changeDataSwitch")
    suspend fun changeDataSwitch(
        @Header("Cookie") cookie: String,
        @Body dataSwitchRequest: DataSwitchRequest
    ): ApiResponse<HoYoLABResponse.ChangeDataSwitchResponse>

    @GET("game_record/genshin/api/dailyNote")
    suspend fun getGenshinDailyNote(
        @Header("DS") ds: String = generateDS(headerInformation = HeaderInformation.OS),
        @Header("Cookie") cookie: String,
        @Header("x-rpc-app_version") xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        @Header("x-rpc-client_type") xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        @Query("role_id") roleId: Long,
        @Query("server") server: String,
    ): ApiResponse<HoYoLABResponse.GenshinDailyNoteResponse>
}