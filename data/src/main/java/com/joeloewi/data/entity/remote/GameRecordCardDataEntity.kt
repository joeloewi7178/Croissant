package com.joeloewi.data.entity.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordCardDataEntity(
    val list: List<GameRecordEntity> = listOf()
)
