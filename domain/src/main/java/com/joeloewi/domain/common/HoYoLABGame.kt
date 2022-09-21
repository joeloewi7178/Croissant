package com.joeloewi.domain.common


enum class HoYoLABGame(
    val gameId: Int,
    val gameIconUrl: String,
    val redemptionCodesUrl: String
) {
    HonkaiImpact3rd(
        gameId = 1,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/bh3_hoyoverse.png",
        redemptionCodesUrl = "https://www.arca.live/b/hk3rd/7334792"
    ),
    GenshinImpact(
        gameId = 2,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/ys_hoyoverse.png",
        redemptionCodesUrl = "https://www.arca.live/b/genshin/30819489"
    ),
    TearsOfThemis(
        gameId = 4,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/nxx_hoyoverse.png",
        redemptionCodesUrl = ""
    ),
    Unknown(
        gameId = -1,
        gameIconUrl = "",
        redemptionCodesUrl = ""
    );

    companion object {
        fun findByGameId(gameId: Int): HoYoLABGame =
            values().find { it.gameId == gameId } ?: Unknown
    }
}