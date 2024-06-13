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

import com.joeloewi.croissant.core.data.model.BaseResponse
import com.joeloewi.croissant.core.data.model.GameRecordCardData
import com.joeloewi.croissant.core.data.model.GenshinDailyNoteData
import com.joeloewi.croissant.core.data.model.UserFullInfo
import com.joeloewi.croissant.data.mapper.GameRecordCardDataMapper
import com.joeloewi.croissant.data.mapper.GenshinDailyNoteDataMapper
import com.joeloewi.croissant.data.mapper.UserFullInfoMapper
import com.joeloewi.croissant.data.util.throwIfNotOk
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: com.joeloewi.croissant.core.network.HoYoLABDataSource,
    private val userFullInfoMapper: UserFullInfoMapper,
    private val gameRecordCardDataMapper: GameRecordCardDataMapper,
    private val genshinDailyNoteDataMapper: GenshinDailyNoteDataMapper
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): Result<UserFullInfo> =
        hoYoLABDataSource.runCatching {
            getUserFullInfo(cookie).getOrThrow().throwIfNotOk()
        }.mapCatching {
            userFullInfoMapper.toDomain(it)
        }

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<GameRecordCardData?> =
        hoYoLABDataSource.runCatching {
            getGameRecordCard(cookie, uid).getOrThrow().throwIfNotOk().data!!
        }.mapCatching {
            gameRecordCardDataMapper.toDomain(it)
        }

    override suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String
    ): Result<GenshinDailyNoteData?> = hoYoLABDataSource.runCatching {
        getGenshinDailyNote(
            cookie = cookie, roleId = roleId, server = server
        ).getOrThrow().throwIfNotOk().data!!
    }.mapCatching {
        genshinDailyNoteDataMapper.toDomain(it)
    }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<BaseResponse> = hoYoLABDataSource.runCatching {
        changeDataSwitch(cookie, switchId, isPublic, gameId).getOrThrow().throwIfNotOk()
    }
}