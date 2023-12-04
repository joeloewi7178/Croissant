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

import com.joeloewi.croissant.data.mapper.GameRecordCardDataMapper
import com.joeloewi.croissant.data.mapper.GenshinDailyNoteDataMapper
import com.joeloewi.croissant.data.mapper.UserFullInfoMapper
import com.joeloewi.croissant.data.repository.remote.HoYoLABDataSource
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.BaseResponse
import com.joeloewi.croissant.domain.entity.GameRecordCardData
import com.joeloewi.croissant.domain.entity.GenshinDailyNoteData
import com.joeloewi.croissant.domain.entity.UserFullInfo
import com.joeloewi.croissant.domain.repository.HoYoLABRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource,
    private val userFullInfoMapper: UserFullInfoMapper,
    private val gameRecordCardDataMapper: GameRecordCardDataMapper,
    private val genshinDailyNoteDataMapper: GenshinDailyNoteDataMapper
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): Result<UserFullInfo> =
        hoYoLABDataSource.runCatching {
            getUserFullInfo(cookie).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }.mapCatching {
            userFullInfoMapper.toDomain(it)
        }

    override suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<GameRecordCardData?> =
        hoYoLABDataSource.runCatching {
            getGameRecordCard(cookie, uid).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }.data!!
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
        ).getOrThrow().also { response ->
            if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                throw HoYoLABUnsuccessfulResponseException(
                    responseMessage = response.message,
                    retCode = response.retCode
                )
            }
        }.data!!
    }.mapCatching {
        genshinDailyNoteDataMapper.toDomain(it)
    }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<BaseResponse> =
        hoYoLABDataSource.runCatching {
            changeDataSwitch(cookie, switchId, isPublic, gameId).getOrThrow().also { response ->
                if (HoYoLABRetCode.findByCode(response.retCode) != HoYoLABRetCode.OK) {
                    throw HoYoLABUnsuccessfulResponseException(
                        responseMessage = response.message,
                        retCode = response.retCode
                    )
                }
            }
        }
}