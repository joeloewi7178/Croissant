package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse

interface HonkaiImpact3rdCheckInDataSource {
    suspend fun attendCheckInHonkaiImpact3rd(cookie: String): ApiResponse<AttendanceResponse>
}