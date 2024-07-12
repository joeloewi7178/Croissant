package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.model.UserInfoEntity

@Immutable
data class UserInfo(
    val uid: Long,
    val nickname: String
)

fun UserInfoEntity.asExternalData(): UserInfo = with(this) { UserInfo(uid, nickname) }
