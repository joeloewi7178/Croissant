package com.joeloewi.croissant.data.remote.dao

import com.joeloewi.croissant.data.common.HeaderInformation
import com.joeloewi.croissant.data.remote.model.common.DataSwitch
import com.joeloewi.croissant.data.remote.model.request.DataSwitchRequest
import com.joeloewi.croissant.data.remote.model.response.*
import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse
import com.joeloewi.croissant.util.generateDS
import okhttp3.ResponseBody
import retrofit2.http.*

interface HoYoLABService {

    @GET("community/user/wapi/getUserFullInfo")
    suspend fun getUserFullInfo(@Header("Cookie") cookie: String): UserFullInfoResponse

    @GET("game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): GameRecordCardResponse

    @POST("game_record/card/wapi/changeDataSwitch")
    suspend fun changeDataSwitch(
        @Header("Cookie") cookie: String,
        @Body dataSwitchRequest: DataSwitchRequest
    ): ChangeDataSwitchResponse

    @GET("game_record/genshin/api/dailyNote")
    suspend fun getGenshinDailyNote(
        @Header("DS") ds: String = generateDS(headerInformation = HeaderInformation.OS),
        @Header("Cookie") cookie: String,
        @Header("x-rpc-app_version") xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        @Header("x-rpc-client_type") xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        @Query("role_id") roleId: Long,
        @Query("server") server: String,
    ): GenshinDailyNoteResponse

    @POST
    suspend fun attendCheckInGenshinImpact(
        @Url url: String = "https://hk4e-api-os.mihoyo.com/event/sol/sign?act_id=e202102251931481&lang=ko-kr",
        @Header("Cookie") cookie: String
    ): AttendanceResponse

    @POST
    suspend fun attendCheckInHonkaiImpact3rd(
        @Url url: String = "https://api-os-takumi.mihoyo.com/event/mani/sign?act_id=e202110291205111&lang=ko-kr",
        @Header("Cookie") cookie: String
    ): AttendanceResponse
}