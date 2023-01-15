package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.HonkaiImpact3rdCheckInService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.repository.remote.HonkaiImpact3rdCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class HonkaiImpact3rdCheckInDataSourceImpl @Inject constructor(
    private val honkaiImpact3rdCheckInService: HonkaiImpact3rdCheckInService,
) : HonkaiImpact3rdCheckInDataSource {
    override suspend fun attendCheckInHonkaiImpact3rd(cookie: String): ApiResponse<AttendanceResponse> =
        honkaiImpact3rdCheckInService.attendCheckInHonkaiImpact3rd(cookie = cookie)
}