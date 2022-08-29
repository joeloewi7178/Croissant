package com.joeloewi.data.api.model.response

import com.joeloewi.data.entity.remote.GenshinDailyNoteDataEntity
import com.joeloewi.domain.entity.BaseResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenshinDailyNoteResponse(
    @Json(name = "retcode")
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: GenshinDailyNoteDataEntity?
) : BaseResponse
