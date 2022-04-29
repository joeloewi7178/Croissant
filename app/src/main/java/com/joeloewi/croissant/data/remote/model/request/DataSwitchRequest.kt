package com.joeloewi.croissant.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataSwitchRequest(
    @Json(name = "switch_id")
    val switchId: Int = Int.MIN_VALUE,
    @Json(name = "is_public")
    val isPublic: Boolean = false,
    @Json(name = "game_id")
    val gameId: Int = Int.MIN_VALUE,
)
