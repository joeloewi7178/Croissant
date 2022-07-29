package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.BaseResponse
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfoResponse
import com.joeloewi.domain.wrapper.ContentOrError

interface HoYoLABRepository {
    suspend fun getUserFullInfo(cookie: String): ContentOrError<UserFullInfoResponse>

    suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): ContentOrError<GameRecordCardData?>

    suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String,
    ): ContentOrError<GenshinDailyNoteData?>

    //uses message, retcode from response
    suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): ContentOrError<BaseResponse>
}