package com.joeloewi.croissant.core.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoDataEntity(
    @Json(name = "user_info")
    val userInfo: UserInfoEntity
)
