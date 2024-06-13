package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.model.DataHoYoLABGame

enum class HoYoLABGame(
    val gameId: Int,
    val gameIconUrl: String
) {
    HonkaiImpact3rd(
        gameId = 1,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/bh3_hoyoverse.png",
    ),
    GenshinImpact(
        gameId = 2,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/ys_hoyoverse.png",
    ),
    TearsOfThemis(
        gameId = 4,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/nxx_hoyoverse.png",
    ),
    HonkaiStarRail(
        gameId = 6,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/starrail_hoyoverse.png"
    ),
    Unknown(
        gameId = -1,
        gameIconUrl = "",
    );

    companion object {
        fun findByGameId(gameId: Int) = entries.find { it.gameId == gameId } ?: Unknown
    }
}

fun DataHoYoLABGame.asExternalData(): HoYoLABGame = when (this) {
    DataHoYoLABGame.HonkaiImpact3rd -> HoYoLABGame.HonkaiImpact3rd
    DataHoYoLABGame.GenshinImpact -> HoYoLABGame.GenshinImpact
    DataHoYoLABGame.TearsOfThemis -> HoYoLABGame.TearsOfThemis
    DataHoYoLABGame.HonkaiStarRail -> HoYoLABGame.HonkaiStarRail
    DataHoYoLABGame.Unknown -> HoYoLABGame.Unknown
}

fun HoYoLABGame.asData(): DataHoYoLABGame = when (this) {
    HoYoLABGame.HonkaiImpact3rd -> DataHoYoLABGame.HonkaiImpact3rd
    HoYoLABGame.GenshinImpact -> DataHoYoLABGame.GenshinImpact
    HoYoLABGame.TearsOfThemis -> DataHoYoLABGame.TearsOfThemis
    HoYoLABGame.HonkaiStarRail -> DataHoYoLABGame.HonkaiStarRail
    HoYoLABGame.Unknown -> DataHoYoLABGame.Unknown
}