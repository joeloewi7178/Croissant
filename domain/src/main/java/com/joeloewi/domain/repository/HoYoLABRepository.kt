package com.joeloewi.domain.repository

import com.joeloewi.domain.entity.*

interface HoYoLABRepository {
    suspend fun getUserFullInfo(cookie: String): UserFullInfoResponse

    suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): GameRecordCardData?

    suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String,
    ): GenshinDailyNoteData?

    //uses message, retcode from response
    suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): BaseResponse

    suspend fun attendCheckInGenshinImpact(
        cookie: String
    ): BaseResponse

    suspend fun attendCheckInHonkaiImpact3rd(
        cookie: String
    ): BaseResponse

    suspend fun attendCheckInTearsOfThemis(
        cookie: String
    ): BaseResponse
}