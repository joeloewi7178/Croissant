package com.joeloewi.croissant.domain.entity

data class FailureLog(
    val id: Long = 0,
    val executionLogId: Long = 0,
    val failureMessage: String = "",
    val failureStackTrace: String = "",
)
