package com.joeloewi.croissant.data.remote.model.common

data class GameRecord(
    val hasRole: Boolean = false,
    val gameId: Int = Int.MIN_VALUE,
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
)
