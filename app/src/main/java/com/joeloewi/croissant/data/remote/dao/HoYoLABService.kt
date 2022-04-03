package com.joeloewi.croissant.data.remote.dao

import com.joeloewi.croissant.data.remote.model.response.AttendanceResponse
import com.joeloewi.croissant.data.remote.model.response.GameRecordCardResponse
import com.joeloewi.croissant.data.remote.model.response.UserFullInfoResponse
import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse
import retrofit2.http.*

interface HoYoLABService {

    @GET("community/user/wapi/getUserFullInfo")
    suspend fun getUserFullInfo(@Header("Cookie") cookie: String): UserFullInfoResponse

    @GET("game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): GameRecordCardResponse

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