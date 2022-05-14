package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.GenshinImpactCheckInService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.repository.remote.GenshinImpactCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenshinImpactCheckInDataSourceImpl @Inject constructor(
    private val genshinImpactCheckInService: GenshinImpactCheckInService,
    private val coroutineDispatcher: CoroutineDispatcher,
) : GenshinImpactCheckInDataSource {
    override suspend fun attendCheckInGenshinImpact(cookie: String): ApiResponse<AttendanceResponse> =
        withContext(coroutineDispatcher) {
            genshinImpactCheckInService.attendCheckInGenshinImpact(cookie = cookie)
        }
}