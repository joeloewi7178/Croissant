package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.TearsOfThemisCheckInService
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.repository.remote.TearsOfThemisCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TearsOfThemisCheckInDataSourceImpl @Inject constructor(
    private val tearsOfThemisCheckInService: TearsOfThemisCheckInService,
    private val coroutineDispatcher: CoroutineDispatcher,
) : TearsOfThemisCheckInDataSource {
    override suspend fun attendCheckInTearsOfThemis(cookie: String): ApiResponse<AttendanceResponse> =
        withContext(coroutineDispatcher) {
            tearsOfThemisCheckInService.attendTearsOfThemis(cookie = cookie)
        }
}