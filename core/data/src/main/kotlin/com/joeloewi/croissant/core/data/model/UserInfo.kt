package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.model.UserInfoEntity

data class UserInfo(
    val uid: Long,
    val nickname: String
)

fun UserInfoEntity.asExternalData(): UserInfo = with(this) { UserInfo(uid, nickname) }
