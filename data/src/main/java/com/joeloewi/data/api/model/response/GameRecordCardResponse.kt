package com.joeloewi.data.api.model.response

import com.joeloewi.data.entity.remote.GameRecordCardDataEntity
import com.joeloewi.domain.entity.BaseResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordCardResponse(
    @Json(name = "retcode")
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: GameRecordCardDataEntity?
) : BaseResponse
