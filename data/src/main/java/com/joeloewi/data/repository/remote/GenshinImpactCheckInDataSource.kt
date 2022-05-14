package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse

interface GenshinImpactCheckInDataSource {
    suspend fun attendCheckInGenshinImpact(cookie: String): ApiResponse<AttendanceResponse>
}