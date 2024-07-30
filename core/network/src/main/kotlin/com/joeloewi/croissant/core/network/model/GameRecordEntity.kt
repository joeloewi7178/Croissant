package com.joeloewi.croissant.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameRecordEntity(
    @SerialName("has_role")
    val hasRole: Boolean = false,
    @SerialName("game_id")
    val gameId: Int = INVALID_GAME_ID,
    @SerialName("game_role_id")
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
    @SerialName("region_name")
    val regionName: String = "",
    val region: String = "",
    @SerialName("data_switches")
    val dataSwitches: List<DataSwitchEntity> = listOf(),
) {
    companion object {
        const val INVALID_GAME_ID = Int.MIN_VALUE
    }
}
