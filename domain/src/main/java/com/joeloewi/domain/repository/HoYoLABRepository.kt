package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfo

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