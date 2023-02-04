package com.joeloewi.croissant.domain.common

enum class HoYoLABRetCode(
    val retCode: Int = Int.MIN_VALUE,
) {
    LoginFailed(retCode = -100),
    OK(retCode = 0),
    AlreadyCheckedIn(retCode = -5003),
    Unknown;

    companion object {
        fun findByCode(retCode: Int) = values().find { it.retCode == retCode } ?: Unknown
    }
}