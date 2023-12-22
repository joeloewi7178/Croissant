package com.joeloewi.croissant.domain.entity

data class UserFullInfo(
    val retCode: Int = Int.MIN_VALUE,
    val message: String = "",
    val data: UserFullInfoData?
)
