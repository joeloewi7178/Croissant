package com.joeloewi.croissant.core.data.model

data class UserFullInfo(
    val retCode: Int = Int.MIN_VALUE,
    val message: String = "",
    val data: UserFullInfoData?
)
