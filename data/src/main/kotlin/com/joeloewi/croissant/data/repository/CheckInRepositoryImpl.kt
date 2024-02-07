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

package com.joeloewi.croissant.data.repository

import com.joeloewi.croissant.data.repository.remote.CheckInDataSource
import com.joeloewi.croissant.data.util.throwIfNotOk
import com.joeloewi.croissant.domain.entity.BaseResponse
import com.joeloewi.croissant.domain.repository.CheckInRepository
import com.skydoves.sandwich.getOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CheckInRepositoryImpl @Inject constructor(
    private val checkInDataSource: CheckInDataSource
) : CheckInRepository {

    override suspend fun attend(actId: String, cookie: String): Result<BaseResponse> =
        withContext(Dispatchers.IO) {
            checkInDataSource.runCatching {
                attend(actId, cookie).getOrThrow().throwIfNotOk()
            }
        }

    override suspend fun attendCheckInGenshinImpact(
        cookie: String
    ): Result<BaseResponse> = withContext(Dispatchers.IO) {
        checkInDataSource.runCatching {
            attendCheckInGenshinImpact(cookie).getOrThrow().throwIfNotOk()
        }
    }

    override suspend fun attendCheckInHonkaiImpact3rd(
        cookie: String
    ): Result<BaseResponse> = withContext(Dispatchers.IO) {
        checkInDataSource.runCatching {
            attendCheckInHonkaiImpact3rd(cookie).getOrThrow().throwIfNotOk()
        }
    }
}