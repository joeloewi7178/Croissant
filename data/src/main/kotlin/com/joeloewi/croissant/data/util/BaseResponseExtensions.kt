package com.joeloewi.croissant.data.util

import com.joeloewi.croissant.core.data.model.BaseResponse
import com.joeloewi.croissant.domain.common.HoYoLABRetCode
import com.joeloewi.croissant.domain.common.exception.HoYoLABUnsuccessfulResponseException

fun <T : com.joeloewi.croissant.core.data.model.BaseResponse> T.throwIfNotOk(): T {
    if (HoYoLABRetCode.findByCode(retCode) != HoYoLABRetCode.OK) {
        throw HoYoLABUnsuccessfulResponseException(
            responseMessage = message,
            retCode = retCode
        )
    }

    return this
}