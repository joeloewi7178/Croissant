package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.model.GenshinDailyNoteDataEntity

data class GenshinDailyNoteData(
    val currentResin: Int = 0,
    val maxResin: Int = 0
)

fun GenshinDailyNoteDataEntity.asExternalData(): GenshinDailyNoteData =
    with(this) { GenshinDailyNoteData(currentResin, maxResin) }

fun GenshinDailyNoteData.asData(): GenshinDailyNoteDataEntity =
    with(this) { GenshinDailyNoteDataEntity(currentResin, maxResin) }