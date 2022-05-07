package com.joeloewi.domain.entity

import com.joeloewi.domain.common.HoYoLABGame

data class Game(
    val id: Long = 0,
    val attendanceId: Long = 0,
    val roleId: Long = 0,
    val type: HoYoLABGame = HoYoLABGame.Unknown,
    val region: String = ""
)
