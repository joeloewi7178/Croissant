package com.joeloewi.croissant.core.network.model.response

import com.joeloewi.croissant.core.network.model.GameRecordCardDataEntity
import com.joeloewi.croissant.core.network.model.GenshinDailyNoteDataEntity
import com.joeloewi.croissant.core.network.model.UserFullInfoDataEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class HoYoLABResponse {
    abstract val retCode: Int
    abstract val message: String
    abstract val data: Any?

    @Serializable
    @SerialName("AttendanceResponse")
    data class AttendanceResponse(
        override val retCode: Int,
        override val message: String,
        @Contextual override val data: Any?
    ): HoYoLABResponse()

    @Serializable
    @SerialName("ChangeDataSwitchResponse")
    data class ChangeDataSwitchResponse(
        override val retCode: Int,
        override val message: String,
        @Contextual override val data: Any?
    ): HoYoLABResponse()

    @Serializable
    @SerialName("GameRecordCardResponse")
    data class GameRecordCardResponse(
        override val retCode: Int,
        override val message: String,
        override val data: GameRecordCardDataEntity?
    ): HoYoLABResponse()

    @Serializable
    @SerialName("GenshinDailyNoteResponse")
    data class GenshinDailyNoteResponse(
        override val retCode: Int,
        override val message: String,
        override val data: GenshinDailyNoteDataEntity?
    ): HoYoLABResponse()

    @Serializable
    @SerialName("UserFullInfoResponse")
    data class UserFullInfoResponse(
        override val retCode: Int,
        override val message: String,
        override val data: UserFullInfoDataEntity?
    ): HoYoLABResponse()
}