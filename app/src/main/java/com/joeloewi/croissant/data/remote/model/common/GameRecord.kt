package com.joeloewi.croissant.data.remote.model.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecord(
    @Json(name = "has_role")
    val hasRole: Boolean = false,
    @Json(name = "game_id")
    val gameId: Int = Int.MIN_VALUE,
    @Json(name = "game_role_id")
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
    @Json(name = "region_name")
    val regionName: String = "",
    val region: String = "",
    @Json(name = "data_switches")
    val dataSwitches: List<DataSwitch> = listOf(),
)
