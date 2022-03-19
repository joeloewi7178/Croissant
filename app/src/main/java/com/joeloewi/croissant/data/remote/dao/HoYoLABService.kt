package com.joeloewi.croissant.data.remote.dao

import com.joeloewi.croissant.data.remote.model.response.GameRecordCardResponse
import com.joeloewi.croissant.data.remote.model.response.UserFullInfoResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface HoYoLABService {

    @GET("community/user/wapi/getUserFullInfo")
    suspend fun getUserFullInfo(@Header("Cookie") cookie: String): UserFullInfoResponse

    @GET("game_record/card/wapi/getGameRecordCard")
    suspend fun getGameRecordCard(
        @Header("Cookie") cookie: String,
        @Query("uid") uid: Long
    ): GameRecordCardResponse
}