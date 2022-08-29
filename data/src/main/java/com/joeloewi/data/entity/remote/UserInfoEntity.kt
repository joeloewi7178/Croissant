package com.joeloewi.data.entity.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfoEntity(
    val uid: Long,
    val nickname: String
)
