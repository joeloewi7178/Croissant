package com.joeloewi.domain.entity

data class GameRecord(
    val hasRole: Boolean = false,
    val gameId: Int = INVALID_GAME_ID,
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
    val regionName: String = "",
    val region: String = "",
    val dataSwitches: List<DataSwitch> = listOf(),
) {
    companion object {
        const val INVALID_GAME_ID = Int.MIN_VALUE
    }
}
