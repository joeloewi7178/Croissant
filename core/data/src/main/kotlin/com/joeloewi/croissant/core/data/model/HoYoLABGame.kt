package com.joeloewi.croissant.core.data.model

import com.joeloewi.croissant.core.model.DataHoYoLABGame

enum class HoYoLABGame(
    val gameId: Int,
    val gameIconUrl: String
) {
    HonkaiImpact3rd(
        gameId = 1,
        gameIconUrl = "https://hyl-static-res-prod.hoyolab.com/communityweb/business/bh3_hoyoverse.png",
    ),
    GenshinImpact(
        gameId = 2,
        gameIconUrl = "https://hyl-static-res-prod.hoyolab.com/communityweb/business/ys_hoyoverse.png",
    ),
    TearsOfThemis(
        gameId = 4,
        gameIconUrl = "https://hyl-static-res-prod.hoyolab.com/communityweb/business/nxx_hoyoverse.png",
    ),
    HonkaiStarRail(
        gameId = 6,
        gameIconUrl = "https://hyl-static-res-prod.hoyolab.com/communityweb/business/starrail_hoyoverse.png"
    ),
    ZenlessZonZero(
        gameId = 8,
        gameIconUrl = "https://hyl-static-res-prod.hoyolab.com/communityweb/business/nap.png"
    ),
    Unknown(
        gameId = -1,
        gameIconUrl = "",
    );

    companion object {
        fun findByGameId(gameId: Int) = entries.find { it.gameId == gameId } ?: Unknown
        fun supportedGames() = entries.filter { it != Unknown }
    }
}

fun DataHoYoLABGame.asExternalData(): HoYoLABGame = when (this) {
    DataHoYoLABGame.HonkaiImpact3rd -> HoYoLABGame.HonkaiImpact3rd
    DataHoYoLABGame.GenshinImpact -> HoYoLABGame.GenshinImpact
    DataHoYoLABGame.TearsOfThemis -> HoYoLABGame.TearsOfThemis
    DataHoYoLABGame.HonkaiStarRail -> HoYoLABGame.HonkaiStarRail
    DataHoYoLABGame.ZenlessZoneZero -> HoYoLABGame.ZenlessZonZero
    DataHoYoLABGame.Unknown -> HoYoLABGame.Unknown
}

fun HoYoLABGame.asData(): DataHoYoLABGame = when (this) {
    HoYoLABGame.HonkaiImpact3rd -> DataHoYoLABGame.HonkaiImpact3rd
    HoYoLABGame.GenshinImpact -> DataHoYoLABGame.GenshinImpact
    HoYoLABGame.TearsOfThemis -> DataHoYoLABGame.TearsOfThemis
    HoYoLABGame.HonkaiStarRail -> DataHoYoLABGame.HonkaiStarRail
    HoYoLABGame.ZenlessZonZero -> DataHoYoLABGame.ZenlessZoneZero
    HoYoLABGame.Unknown -> DataHoYoLABGame.Unknown
}