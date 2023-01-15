package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.GenshinImpactCheckInService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.repository.remote.GenshinImpactCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class GenshinImpactCheckInDataSourceImpl @Inject constructor(
    private val genshinImpactCheckInService: GenshinImpactCheckInService,
) : GenshinImpactCheckInDataSource {
    override suspend fun attendCheckInGenshinImpact(cookie: String): ApiResponse<AttendanceResponse> =
        genshinImpactCheckInService.attendCheckInGenshinImpact(cookie = cookie)
}