package com.joeloewi.croissant.domain.entity

import com.joeloewi.croissant.domain.common.HoYoLABGame

data class FailureLog(
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
