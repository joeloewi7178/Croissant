package com.joeloewi.domain.common.exception

sealed class HoYoLABException(
    override val message: String? = null
) : Exception() {

    object LoginFailedException : HoYoLABException(
        message = "Login Failed. Please try again."
    )

    object AlreadyCheckedInException : HoYoLABException(
        message = "Already Checked in today"
    )

    class Unknown(
        responseMessage: String,
        retCode: Int,
        override val message: String? = "Server responded with unknown values: message=${responseMessage}, retCode=${retCode}"
    ) : HoYoLABException()
}