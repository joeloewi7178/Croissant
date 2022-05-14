package com.joeloewi.data.api.dao

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface TearsOfThemisCheckInService {
    @POST("event/luna/os/sign")
    suspend fun attendTearsOfThemis(
        @Query("act_id") actId: String = "e202202281857121",
        @Query("lang") language: String = Locale.getDefault().toLanguageTag().lowercase(),
        @Header("Cookie") cookie: String
    ): ApiResponse<AttendanceResponse>
}