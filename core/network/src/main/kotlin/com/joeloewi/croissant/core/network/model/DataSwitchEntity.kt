package com.joeloewi.croissant.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataSwitchEntity(
    @SerialName("switch_id")
    val switchId: Int = Int.MIN_VALUE,
    @SerialName("is_public")
    val isPublic: Boolean = false,
    @SerialName("switch_name")
    val switchName: String = ""
)