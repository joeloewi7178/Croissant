package com.joeloewi.croissant.data.remote.model.response

import com.joeloewi.croissant.data.remote.model.common.UserFullInfoData
import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoResponse(
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: UserFullInfoData?
) : BaseResponse
