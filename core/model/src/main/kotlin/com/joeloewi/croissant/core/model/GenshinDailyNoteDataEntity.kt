package com.joeloewi.croissant.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenshinDailyNoteDataEntity(
    @Json(name = "current_resin")
    val currentResin: Int = 0,
    @Json(name = "max_resin")
    val maxResin: Int = 0
)
