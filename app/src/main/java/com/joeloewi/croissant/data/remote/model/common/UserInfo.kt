package com.joeloewi.croissant.data.remote.model.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo(
    val uid: Long,
    val nickname: String
)
