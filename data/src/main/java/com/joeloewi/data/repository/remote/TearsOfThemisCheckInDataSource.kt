package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.skydoves.sandwich.ApiResponse

interface TearsOfThemisCheckInDataSource {
    suspend fun attendCheckInTearsOfThemis(cookie: String): ApiResponse<AttendanceResponse>
}