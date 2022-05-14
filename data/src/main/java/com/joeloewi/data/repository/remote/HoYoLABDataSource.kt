package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.data.api.model.response.GameRecordCardResponse
import com.joeloewi.data.api.model.response.GenshinDailyNoteResponse
import com.joeloewi.data.common.HeaderInformation
import com.joeloewi.data.common.generateDS
import com.joeloewi.domain.entity.UserFullInfoResponse
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