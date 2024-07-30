package com.joeloewi.croissant.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFullInfoDataEntity(
    @SerialName("user_info")
    val userInfo: UserInfoEntity
)
