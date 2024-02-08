package com.joeloewi.croissant.data.util

import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException
import com.joeloewi.croissant.domain.entity.BaseResponse

fun <T : BaseResponse> T.throwIfNotOk(): T {
    if (HoYoLABRetCode.findByCode(retCode) != HoYoLABRetCode.OK) {
        throw HoYoLABUnsuccessfulResponseException(
            responseMessage = message,
            retCode = retCode
        )
    }

    return this
}