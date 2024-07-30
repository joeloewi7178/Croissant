package com.joeloewi.croissant.core.data.model

import androidx.compose.runtime.Immutable
import com.joeloewi.croissant.core.network.model.GameRecordEntity

@Immutable
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

fun GameRecordEntity.asExternalData(): GameRecord = with(this) {
    GameRecord(hasRole, gameId, gameRoleId, nickname, level, regionName, region)
}

fun GameRecord.asData(): GameRecordEntity = with(this) {
    GameRecordEntity(hasRole, gameId, gameRoleId, nickname, level, regionName, region)
}
