package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.database.model.ResultRangeEntity

data class ResultRange(
    val start: Long,
    val end: Long
)

fun ResultRangeEntity.asExternalData(): ResultRange = with(this) {
    ResultRange(start, end)
}