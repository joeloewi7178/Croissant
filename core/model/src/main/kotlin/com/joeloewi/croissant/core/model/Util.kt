package com.joeloewi.croissant.core.model

import kotlin.coroutines.cancellation.CancellationException

inline fun <reified T : Throwable, R> Result<R>.except(): Result<R> =
    onFailure { if (it is T) throw it }

fun <R> Result<R>.exceptCancellationException(): Result<R> = except<CancellationException, _>()

fun <T : BaseResponse> T.throwIfNotOk(): T {
    if (HoYoLABRetCode.findByCode(retCode) != HoYoLABRetCode.OK) {
        throw HoYoLABUnsuccessfulResponseException(
            responseMessage = message,
            retCode = retCode
        )
    }

    return this
}