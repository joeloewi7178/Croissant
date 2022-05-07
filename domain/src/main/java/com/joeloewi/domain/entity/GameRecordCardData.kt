package com.joeloewi.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordCardData(
    val list: List<GameRecord> = listOf()
)
