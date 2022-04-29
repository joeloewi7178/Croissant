package com.joeloewi.croissant.data.remote.model.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenshinDailyNoteData(
    @Json(name = "current_resin")
    val currentResin: Int = 0,
    @Json(name = "max_resin")
    val maxResin: Int = 0
)
