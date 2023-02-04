/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.data.repository.remote.impl

import com.joeloewi.croissant.data.api.dao.TearsOfThemisCheckInService
import com.joeloewi.croissant.data.api.model.response.AttendanceResponse
import com.joeloewi.croissant.data.repository.remote.TearsOfThemisCheckInDataSource
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class TearsOfThemisCheckInDataSourceImpl @Inject constructor(
    private val tearsOfThemisCheckInService: TearsOfThemisCheckInService,
) : TearsOfThemisCheckInDataSource {
    override suspend fun attendCheckInTearsOfThemis(cookie: String): ApiResponse<AttendanceResponse> =
        tearsOfThemisCheckInService.attendTearsOfThemis(cookie = cookie)
}