package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.AttendanceResponse
import com.joeloewi.data.api.model.response.ChangeDataSwitchResponse
import com.joeloewi.data.common.HeaderInformation
import com.joeloewi.data.common.generateDS
import com.joeloewi.domain.entity.GameRecordCardData
import com.joeloewi.domain.entity.GenshinDailyNoteData
import com.joeloewi.domain.entity.UserFullInfoResponse
import java.util.*

interface HoYoLABDataSource {
    suspend fun getUserFullInfo(cookie: String): UserFullInfoResponse

    suspend fun getGameRecordCard(
        cookie: String,
        uid: Long
    ): GameRecordCardData?

    suspend fun getGenshinDailyNote(
        ds: String = generateDS(headerInformation = HeaderInformation.OS),
        cookie: String,
        xRpcAppVersion: String = HeaderInformation.OS.xRpcAppVersion,
        xRpcClientType: String = HeaderInformation.OS.xRpcClientType,
        roleId: Long,
        server: String,
    ): GenshinDailyNoteData?

    suspend fun changeDataSwitch(
        cookie: String,
        switchId: Int,
        isPublic: Boolean,
        gameId: Int
    ): ChangeDataSwitchResponse

    suspend fun attendCheckInGenshinImpact(
        url: String = "https://hk4e-api-os.mihoyo.com/event/sol/sign?act_id=e202102251931481&lang=${
            Locale.getDefault().toLanguageTag().lowercase()
        }",
        cookie: String
    ): AttendanceResponse

    suspend fun attendCheckInHonkaiImpact3rd(
        url: String = "https://api-os-takumi.mihoyo.com/event/mani/sign?act_id=e202110291205111&lang=${
            Locale.getDefault().toLanguageTag().lowercase()
        }",
        cookie: String
    ): AttendanceResponse

    suspend fun attendTearsOfThemis(
        url: String = "https://sg-public-api.hoyolab.com/event/luna/os/sign?act_id=e202202281857121&lang=${
            Locale.getDefault().toLanguageTag().lowercase()
        }",
        cookie: String
    ): AttendanceResponse
}