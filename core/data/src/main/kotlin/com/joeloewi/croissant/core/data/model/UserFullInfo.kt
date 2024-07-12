package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserFullInfo(
    val retCode: Int = Int.MIN_VALUE,
    val message: String = "",
    val data: UserFullInfoData?
)
