package com.joeloewi.data.entity.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoDataEntity(
    @Json(name = "user_info")
    val userInfo: UserInfoEntity
)
