package com.joeloewi.croissant.core.data.model.exception

import com.joeloewi.croissant.core.data.model.HoYoLABRetCode
import com.joeloewi.croissant.core.model.BaseResponse

class HoYoLABUnsuccessfulResponseException(
    val responseMessage: String,
    val retCode: Int,
    override val message: String? = "Server responded unsuccessfully: message=${responseMessage}, retCode=${retCode}"
) : Exception()

fun <T : BaseResponse> T.throwIfNotOk(): T {
    if (HoYoLABRetCode.findByCode(retCode) != HoYoLABRetCode.OK) {
        throw HoYoLABUnsuccessfulResponseException(
            responseMessage = message,
            retCode = retCode
        )
    }

    return this
}