package com.joeloewi.domain.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserFullInfoData(
    @Json(name = "user_info")
    val userInfo: UserInfo
)
