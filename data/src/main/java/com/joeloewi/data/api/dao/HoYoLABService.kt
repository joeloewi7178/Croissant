package com.joeloewi.data.api.dao

import com.joeloewi.data.api.model.request.DataSwitchRequest
import com.joeloewi.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.data.api.model.response.GameRecordCardResponse
import com.joeloewi.data.api.model.response.GenshinDailyNoteResponse
import com.joeloewi.data.api.model.response.UserFullInfoResponse
import com.joeloewi.data.common.HeaderInformation
import com.joeloewi.data.common.generateDS
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*

interface HoYoLABService {

    @GET("community/user/wapi/getUserFullInfo")
    suspend fun getUserFullInfo(@Header("Cookie") cookie: String): ApiResponse<UserFullInfoResponse>

    @GET("game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): ApiResponse<GameRecordCardResponse>

    @POST("game_record/card/wapi/changeDataSwitch")
    suspend fun changeDataSwitch(
        @Header("Cookie") cookie: String,
        @Body dataSwitchRequest: DataSwitchRequest
    ): ApiResponse<ChangeDataSwitchResponse>

    @GET("game_record/genshin/api/dailyNote")
    suspend fun getGenshinDailyNote(
        @Header("DS") ds: String = generateDS(headerInformation = HeaderInformation.OS),
        @Header("Cookie") cookie: String,
        @Header("x-rpc-app_version") xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        @Header("x-rpc-client_type") xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        @Query("role_id") roleId: Long,
        @Query("server") server: String,
    ): ApiResponse<GenshinDailyNoteResponse>
}