package com.joeloewi.croissant.data.api.dao

import com.joeloewi.croissant.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.Locale

interface GenshinImpactCheckInService {

    @POST("event/sol/sign")
    suspend fun attend(
        @Header("Cookie") cookie: String,
        @Body params: Map<String, String> = mapOf(
            "act_id" to "e202102251931481",
            "lang" to Locale.getDefault().toLanguageTag().lowercase()
        )
    ): ApiResponse<AttendanceResponse>
}