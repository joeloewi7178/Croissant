package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.HoYoLABService
import com.joeloewi.data.api.model.request.DataSwitchRequest
import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.data.common.GenshinImpactServer
import com.joeloewi.data.common.HeaderInformation
import com.joeloewi.data.common.generateDS
import com.joeloewi.data.repository.remote.HoYoLABDataSource
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HoYoLABDataSourceImpl @Inject constructor(
    private val hoYoLABService: HoYoLABService,
    private val coroutineDispatcher: CoroutineDispatcher
) : HoYoLABDataSource {

    override suspend fun getUserFullInfo(cookie: String): UserFullInfoResponse =
        withContext(coroutineDispatcher) {
            hoYoLABService.getUserFullInfo(cookie)
        }

    override suspend fun getGameRecordCard(cookie: String, uid: Long): GameRecordCardData? =
        withContext(coroutineDispatcher) {
            hoYoLABService.getGameRecordCard(cookie, uid).data
        }

    override suspend fun getGenshinDailyNote(
        ds: String,
        cookie: String,
        xRpcAppVersion: String,
        xRpcClientType: String,
        roleId: Long,
        server: String
    ): GenshinDailyNoteData? = withContext(coroutineDispatcher) {
        val headerInformation = when (GenshinImpactServer.findByRegion(server)) {
            GenshinImpactServer.CNServer -> {
                HeaderInformation.CN
            }
            GenshinImpactServer.Unknown -> {
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
        ).data
    }

    override suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): ChangeDataSwitchResponse = withContext(coroutineDispatcher) {
        hoYoLABService.changeDataSwitch(
            cookie, DataSwitchRequest(
                switchId, isPublic, gameId
            )
        )
    }

    override suspend fun attendCheckInGenshinImpact(
        url: String,
        cookie: String
    ): AttendanceResponse = withContext(coroutineDispatcher) {
        hoYoLABService.attendCheckInGenshinImpact(url, cookie)
    }

    override suspend fun attendCheckInHonkaiImpact3rd(
        url: String,
        cookie: String
    ): AttendanceResponse = withContext(coroutineDispatcher) {
        hoYoLABService.attendCheckInHonkaiImpact3rd(url, cookie)
    }

    override suspend fun attendTearsOfThemis(url: String, cookie: String): AttendanceResponse =
        withContext(coroutineDispatcher) {
            hoYoLABService.attendTearsOfThemis(url, cookie)
        }
}