package com.joeloewi.croissant.core.network.model


import kotlinx.serialization.Serializable

@Serializable
data class GameRecordCardDataEntity(
    val list: List<GameRecordEntity> = listOf()
)
