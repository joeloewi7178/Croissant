package com.joeloewi.croissant.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordEntity(
    @Json(name = "has_role")
    val hasRole: Boolean = false,
    @Json(name = "game_id")
    val gameId: Int = INVALID_GAME_ID,
    @Json(name = "game_role_id")
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
    @Json(name = "region_name")
    val regionName: String = "",
    val region: String = "",
    @Json(name = "data_switches")
    val dataSwitches: List<DataSwitchEntity> = listOf(),
) {
    companion object {
        const val INVALID_GAME_ID = Int.MIN_VALUE
    }
}
