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

import com.joeloewi.croissant.data.repository.remote.GenshinImpactCheckInDataSource
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.BaseResponse
import com.joeloewi.croissant.domain.repository.GenshinImpactCheckInRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class GenshinImpactCheckInRepositoryImpl @Inject constructor(
    private val genshinImpactCheckInDataSource: GenshinImpactCheckInDataSource
) : GenshinImpactCheckInRepository {

    override suspend fun attendCheckInGenshinImpact(cookie: String): Result<BaseResponse> =
        genshinImpactCheckInDataSource.runCatching {
            attendCheckInGenshinImpact(cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }
}