package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.model.GenshinDailyNoteDataEntity

@Immutable
data class GenshinDailyNoteData(
    val currentResin: Int = 0,
    val maxResin: Int = 0
)

fun GenshinDailyNoteDataEntity.asExternalData(): GenshinDailyNoteData =
    with(this) { GenshinDailyNoteData(currentResin, maxResin) }

fun GenshinDailyNoteData.asData(): GenshinDailyNoteDataEntity =
    with(this) { GenshinDailyNoteDataEntity(currentResin, maxResin) }