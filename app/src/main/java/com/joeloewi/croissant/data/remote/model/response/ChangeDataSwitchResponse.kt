package com.joeloewi.croissant.data.remote.model.response

import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangeDataSwitchResponse(
    override val retcode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: Any? = null
) : BaseResponse