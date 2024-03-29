package com.joeloewi.croissant.data.entity.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataSwitchEntity(
    @Json(name = "switch_id")
    val switchId: Int = Int.MIN_VALUE,
    @Json(name = "is_public")
    val isPublic: Boolean = false,
    @Json(name = "switch_name")
    val switchName: String = ""
)