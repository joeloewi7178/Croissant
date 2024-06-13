package com.joeloewi.croissant.core.data.model

data class ResultCount(
    val date: String,
    val successCount: Long = 0,
    val failureCount: Long = 0
)