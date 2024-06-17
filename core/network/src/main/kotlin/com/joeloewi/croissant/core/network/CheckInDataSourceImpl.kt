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

package com.joeloewi.croissant.core.network

import com.joeloewi.croissant.core.common.exceptCancellationException
import com.joeloewi.croissant.core.network.dao.CheckInService
import com.joeloewi.croissant.core.network.dao.GenshinImpactCheckInService
import com.joeloewi.croissant.core.network.model.response.AttendanceResponse
import com.skydoves.sandwich.getOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckInDataSourceImpl @Inject constructor(
    private val checkInService: CheckInService,
    private val genshinImpactCheckInService: GenshinImpactCheckInService
) : CheckInDataSource {

    override suspend fun attend(
        actId: String,
        cookie: String
    ): Result<AttendanceResponse> =
        runCatching {
            withContext(Dispatchers.IO) {
                checkInService.attendCommon(actId = actId, cookie = cookie).getOrThrow()
            }
        }.exceptCancellationException()

    override suspend fun attendCheckInGenshinImpact(
        cookie: String
    ): Result<AttendanceResponse> = runCatching {
        withContext(Dispatchers.IO) {
            genshinImpactCheckInService.attend(cookie = cookie).getOrThrow()
        }
    }.exceptCancellationException()

    override suspend fun attendCheckInHonkaiImpact3rd(
        cookie: String
    ): Result<AttendanceResponse> = runCatching {
        withContext(Dispatchers.IO) {
            checkInService.attendCheckInHonkaiImpact3rd(cookie = cookie).getOrThrow()
        }
    }.exceptCancellationException()
}