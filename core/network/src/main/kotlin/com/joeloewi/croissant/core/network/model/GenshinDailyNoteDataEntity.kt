package com.joeloewi.croissant.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenshinDailyNoteDataEntity(
    @SerialName("current_resin")
    val currentResin: Int = 0,
    @SerialName("max_resin")
    val maxResin: Int = 0
)
