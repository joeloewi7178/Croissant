package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.domain.entity.*
import com.joeloewi.domain.repository.HoYoLABRepository
import javax.inject.Inject

class HoYoLABRepositoryImpl @Inject constructor(
    private val hoYoLABDataSource: HoYoLABDataSource
) : HoYoLABRepository {

    override suspend fun getUserFullInfo(cookie: String): UserFullInfoResponse =
        hoYoLABDataSource.getUserFullInfo(cookie)

    override suspend fun getGameRecordCard(cookie: String, uid: Long): GameRecordCardData? =
        hoYoLABDataSource.getGameRecordCard(cookie, uid)

    override suspend fun getGenshinDailyNote(
        cookie: String,
        roleId: Long,
        server: String
    ): GenshinDailyNoteData? = hoYoLABDataSource.getGenshinDailyNote(
        cookie = cookie, roleId = roleId, server = server
    )

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): BaseResponse = hoYoLABDataSource.changeDataSwitch(cookie, switchId, isPublic, gameId)

    override suspend fun attendCheckInGenshinImpact(cookie: String): BaseResponse =
        hoYoLABDataSource.attendCheckInGenshinImpact(
            cookie = cookie
        )

    override suspend fun attendCheckInHonkaiImpact3rd(cookie: String): BaseResponse =
        hoYoLABDataSource.attendCheckInHonkaiImpact3rd(
            cookie = cookie
        )

    override suspend fun attendCheckInTearsOfThemis(cookie: String): BaseResponse =
        hoYoLABDataSource.attendTearsOfThemis(cookie = cookie)
}