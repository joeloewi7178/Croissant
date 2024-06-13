package com.joeloewi.croissant.core.data.model

data class GameRecord(
    val hasRole: Boolean = false,
    val gameId: Int = com.joeloewi.croissant.core.data.model.GameRecord.Companion.INVALID_GAME_ID,
    val gameRoleId: Long = Long.MIN_VALUE,
    val nickname: String = "",
    val level: Int = Int.MIN_VALUE,
    val regionName: String = "",
    val region: String = "",
    val dataSwitches: List<com.joeloewi.croissant.core.data.model.DataSwitch> = listOf(),
) {
    companion object {
        const val INVALID_GAME_ID = Int.MIN_VALUE
    }
}
