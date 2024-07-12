package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.database.model.ResultCountEntity

@Immutable
data class ResultCount(
    val date: String,
    val successCount: Long = 0,
    val failureCount: Long = 0
)

fun ResultCountEntity.asExternalData(): ResultCount = with(this) {
    ResultCount(date, successCount, failureCount)
}