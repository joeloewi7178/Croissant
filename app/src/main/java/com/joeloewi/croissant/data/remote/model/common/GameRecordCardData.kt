package com.joeloewi.croissant.data.remote.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GameRecordCardData(
    val list: List<GameRecord> = listOf()
)
