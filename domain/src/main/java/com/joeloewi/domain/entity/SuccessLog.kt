package com.joeloewi.domain.entity

import com.joeloewi.domain.common.HoYoLABGame

data class SuccessLog(
    val id: Long = 0,
    val executionLogId: Long = 0,
    val gameName: HoYoLABGame = HoYoLABGame.Unknown,
    val retCode: Int = 0,
    val message: String = ""
)
