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

import com.joeloewi.croissant.data.api.dao.CheckInService
import com.joeloewi.croissant.data.api.dao.GenshinImpactCheckInService
import com.joeloewi.croissant.data.api.model.response.AttendanceResponse
import com.joeloewi.croissant.data.repository.remote.CheckInDataSource
import com.joeloewi.croissant.data.util.executeAndAwait
import com.joeloewi.croissant.data.util.runAndRetryWithExponentialBackOff
import com.skydoves.sandwich.ApiResponse
import javax.inject.Inject

class CheckInDataSourceImpl @Inject constructor(
    private val checkInService: CheckInService,
    private val genshinImpactCheckInService: GenshinImpactCheckInService
) : CheckInDataSource {

    override suspend fun attend(actId: String, cookie: String): ApiResponse<AttendanceResponse> =
        runAndRetryWithExponentialBackOff {
            checkInService.attendCommon(actId = actId, cookie = cookie).executeAndAwait()
        }

    override suspend fun attendCheckInGenshinImpact(
        cookie: String
    ): ApiResponse<AttendanceResponse> = runAndRetryWithExponentialBackOff {
        genshinImpactCheckInService.attend(cookie = cookie).executeAndAwait()
    }

    override suspend fun attendCheckInHonkaiImpact3rd(
        cookie: String
    ): ApiResponse<AttendanceResponse> = runAndRetryWithExponentialBackOff {
        checkInService.attendCheckInHonkaiImpact3rd(cookie = cookie).executeAndAwait()
    }
}