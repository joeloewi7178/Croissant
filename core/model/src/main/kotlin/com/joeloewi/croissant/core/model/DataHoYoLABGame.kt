package com.joeloewi.croissant.core.model

enum class DataHoYoLABGame(
    val gameId: Int
) {
    HonkaiImpact3rd(
        gameId = 1
    ),
    GenshinImpact(
        gameId = 2
    ),
    TearsOfThemis(
        gameId = 4
    ),
    HonkaiStarRail(
        gameId = 6
    ),
    ZenlessZoneZero(
        gameId = 8
    ),
    Unknown(
        gameId = -1
    );

    companion object {
        fun findByGameId(gameId: Int) =
            entries.find { it.gameId == gameId } ?: Unknown
    }
}