package com.joeloewi.data.api.dao

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface HonkaiImpact3rdCheckInService {
    @POST("event/mani/sign")
    suspend fun attendCheckInHonkaiImpact3rd(
        @Query("act_id") actId: String = "e202110291205111",
        @Query("lang") language: String = Locale.getDefault().toLanguageTag().lowercase(),
        @Header("Cookie") cookie: String
    ): ApiResponse<AttendanceResponse>
}