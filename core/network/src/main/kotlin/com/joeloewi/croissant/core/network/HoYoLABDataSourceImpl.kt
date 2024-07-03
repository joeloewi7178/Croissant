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

import com.joeloewi.croissant.core.network.dao.HoYoLABService
import com.joeloewi.croissant.core.network.model.request.DataSwitchRequest
import com.joeloewi.croissant.core.network.model.response.ChangeDataSwitchResponse
import com.joeloewi.croissant.core.network.model.response.GameRecordCardResponse
import com.joeloewi.croissant.core.network.model.response.GenshinDailyNoteResponse
import com.joeloewi.croissant.core.network.model.response.UserFullInfoResponse
import com.skydoves.sandwich.getOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HoYoLABDataSourceImpl @Inject constructor(
    private val hoYoLABService: HoYoLABService,
) : HoYoLABDataSource {
    override suspend fun getUserFullInfo(cookie: String): Result<UserFullInfoResponse> =
        withContext(Dispatchers.IO) {
            runCatching { hoYoLABService.getUserFullInfo(cookie).getOrThrow() }
        }

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<GameRecordCardResponse> = withContext(Dispatchers.IO) {
        runCatching { hoYoLABService.getGameRecordCard(cookie, uid).getOrThrow() }
    }

    override suspend fun getGenshinDailyNote(
        ds: String,
        cookie: String,
        xRpcAppVersion: String,
        xRpcClientType: String,
        roleId: Long,
        server: String
    ): Result<GenshinDailyNoteResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val headerInformation =
                when (com.joeloewi.croissant.core.common.GenshinImpactServer.findByRegion(server)) {
                    com.joeloewi.croissant.core.common.GenshinImpactServer.CNServer -> {
                        HeaderInformation.CN
                    }

                    com.joeloewi.croissant.core.common.GenshinImpactServer.Unknown -> {
                        HeaderInformation.CN
                    }

                    else -> {
                        HeaderInformation.OS
                    }
                }

            hoYoLABService.getGenshinDailyNote(
                generateDS(headerInformation),
                cookie,
                xRpcAppVersion = headerInformation.xRpcAppVersion,
                xRpcClientType = headerInformation.xRpcClientType,
                roleId,
                server
            ).getOrThrow()
        }
    }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<ChangeDataSwitchResponse> = withContext(Dispatchers.IO) {
        runCatching {
            hoYoLABService.changeDataSwitch(
                cookie, DataSwitchRequest(
                    switchId, isPublic, gameId
                )
            ).getOrThrow()
        }
    }
}