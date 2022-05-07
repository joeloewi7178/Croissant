package com.joeloewi.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfo(
    val uid: Long,
    val nickname: String
)
