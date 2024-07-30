package com.joeloewi.croissant.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoEntity(
    val uid: Long,
    val nickname: String
)
