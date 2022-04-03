package com.joeloewi.croissant.data.remote.model.response

import com.joeloewi.croissant.data.remote.model.response.base.BaseResponse

data class AttendanceResponse(
    override val retCode: Int = Int.MIN_VALUE,
    override val message: String = "",
    override val data: String? = null
): BaseResponse
