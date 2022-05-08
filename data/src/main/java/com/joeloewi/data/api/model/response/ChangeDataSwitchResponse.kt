package com.joeloewi.data.api.model.response

import com.joeloewi.domain.entity.BaseResponse
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangeDataSwitchResponse(
    override val retcode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: Any? = null
) : BaseResponse
