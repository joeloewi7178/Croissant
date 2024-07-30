package com.joeloewi.croissant.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataSwitchRequest(
    @SerialName("switch_id")
    val switchId: Int = Int.MIN_VALUE,
    @SerialName("is_public")
    val isPublic: Boolean = false,
    @SerialName("game_id")
    val gameId: Int = Int.MIN_VALUE,
)
