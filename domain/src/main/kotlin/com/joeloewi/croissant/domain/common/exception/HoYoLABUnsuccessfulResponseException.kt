package com.joeloewi.croissant.domain.common.exception

class HoYoLABUnsuccessfulResponseException(
    val responseMessage: String,
    val retCode: Int,
    override val message: String? = "Server responded unsuccessfully: message=${responseMessage}, retCode=${retCode}"
) : Exception()