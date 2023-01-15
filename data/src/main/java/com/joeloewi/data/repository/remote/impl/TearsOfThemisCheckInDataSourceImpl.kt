package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.TearsOfThemisCheckInService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class TearsOfThemisCheckInDataSourceImpl @Inject constructor(
    private val tearsOfThemisCheckInService: TearsOfThemisCheckInService,
) : TearsOfThemisCheckInDataSource {
    override suspend fun attendCheckInTearsOfThemis(cookie: String): ApiResponse<AttendanceResponse> =
        tearsOfThemisCheckInService.attendTearsOfThemis(cookie = cookie)
}