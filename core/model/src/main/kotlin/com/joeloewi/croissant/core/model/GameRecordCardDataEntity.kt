package com.joeloewi.croissant.core.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordCardDataEntity(
    val list: List<GameRecordEntity> = listOf()
)
