package com.joeloewi.croissant.core.data.model

data class FailureLog(
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
