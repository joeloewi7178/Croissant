package com.joeloewi.croissant.core.database.model

data class ResultCountEntity(
    val date: String,
    val successCount: Long = 0,
    val failureCount: Long = 0
)