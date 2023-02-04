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

package com.joeloewi.croissant.data.repository.remote

import com.joeloewi.croissant.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.croissant.data.api.model.response.GameRecordCardResponse
import com.joeloewi.croissant.data.api.model.response.GenshinDailyNoteResponse
import com.joeloewi.croissant.data.api.model.response.UserFullInfoResponse
import com.joeloewi.croissant.data.common.HeaderInformation
import com.joeloewi.croissant.data.common.generateDS
import com.skydoves.sandwich.ApiResponse

interface HoYoLABDataSource {
    suspend fun getUserFullInfo(cookie: String): ApiResponse<UserFullInfoResponse>

    suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): ApiResponse<GameRecordCardResponse>

    suspend fun getGenshinDailyNote(
        ds: String = generateDS(headerInformation = HeaderInformation.OS),
        cookie: String,
        xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        roleId: Long,
        server: String,
    ): ApiResponse<GenshinDailyNoteResponse>

    suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): ApiResponse<ChangeDataSwitchResponse>
}