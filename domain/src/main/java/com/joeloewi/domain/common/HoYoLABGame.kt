package com.joeloewi.domain.common


enum class HoYoLABGame(
    val gameId: Int,
    val gameIconUrl: String,
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
    Unknown(
        gameId = -1,
        gameIconUrl = "",
    );

    companion object {
        fun findByGameId(gameId: Int): HoYoLABGame =
            values().find { it.gameId == gameId } ?: Unknown
    }
}