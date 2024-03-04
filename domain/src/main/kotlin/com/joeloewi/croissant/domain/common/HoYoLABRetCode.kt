package com.joeloewi.croissant.domain.common

enum class HoYoLABRetCode(
    val retCode: Int = Int.MIN_VALUE,
) {
    LoginFailed(retCode = -100),
    OK(retCode = 0),
    AlreadyCheckedIn(retCode = -5003),
    CharacterNotExists(retCode = -10002),
    TooManyRequests(retCode = -500004),
    TooManyRequestsGenshinImpact(retCode = -1004),
    Unknown;

    companion object {
        fun findByCode(retCode: Int) = entries.find { it.retCode == retCode } ?: Unknown
    }
}