package com.joeloewi.croissant.domain.entity

data class ResultCount(
    val date: String,
    val successCount: Long = 0,
    val failureCount: Long = 0
)