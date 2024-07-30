package com.joeloewi.croissant.core.network.dao

import com.joeloewi.croissant.core.network.model.response.HoYoLABResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.Locale

interface GenshinImpactCheckInService {

    @POST("event/sol/sign")
    suspend fun attend(
        @Query("lang") language: String = Locale.getDefault().toLanguageTag().lowercase(),
        @Header("Cookie") cookie: String,
        @Body params: Map<String, String> = mapOf("act_id" to "e202102251931481")
    ): ApiResponse<HoYoLABResponse.AttendanceResponse>
}