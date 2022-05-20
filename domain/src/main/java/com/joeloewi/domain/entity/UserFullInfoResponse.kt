package com.joeloewi.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoResponse(
    @Json(name = "retcode")
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: UserFullInfoData?
) : BaseResponse
