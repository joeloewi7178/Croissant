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

package com.joeloewi.croissant.core.data.repository

import com.joeloewi.croissant.core.data.model.GameRecord
import com.joeloewi.croissant.core.data.model.GenshinDailyNoteData
import com.joeloewi.croissant.core.data.model.UserInfo
import com.joeloewi.croissant.core.data.model.asExternalData
import com.joeloewi.croissant.core.model.BaseResponse
import com.joeloewi.croissant.core.network.HoYoLABDataSource
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): Result<UserInfo> =
        hoYoLABDataSource.getUserFullInfo(cookie).mapCatching {
            it.data!!.userInfo.asExternalData()
        }

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<List<GameRecord>> =
        hoYoLABDataSource.getGameRecordCard(cookie, uid).mapCatching {
            it.data!!.list.map { list -> list.asExternalData() }
        }

    override suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String
    ): Result<GenshinDailyNoteData?> = hoYoLABDataSource.getGenshinDailyNote(
        cookie = cookie, roleId = roleId, server = server
    ).mapCatching { it.data!!.asExternalData() }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<BaseResponse> = hoYoLABDataSource.changeDataSwitch(cookie, switchId, isPublic, gameId)
}