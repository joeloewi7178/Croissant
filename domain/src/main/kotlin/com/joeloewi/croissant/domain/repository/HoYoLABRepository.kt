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

package com.joeloewi.croissant.domain.repository

import com.joeloewi.croissant.domain.entity.BaseResponse
import com.joeloewi.croissant.domain.entity.GameRecordCardData
import com.joeloewi.croissant.domain.entity.GenshinDailyNoteData
import com.joeloewi.croissant.domain.entity.UserFullInfo

interface HoYoLABRepository {
    suspend fun getUserFullInfo(cookie: String): Result<UserFullInfo>

    suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): Result<GameRecordCardData?>

    suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String,
    ): Result<GenshinDailyNoteData?>

    //uses message, retcode from response
    suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): Result<BaseResponse>
}