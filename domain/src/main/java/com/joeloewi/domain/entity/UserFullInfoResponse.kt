package com.joeloewi.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoResponse(
    override val retcode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: UserFullInfoData?
) : BaseResponse
