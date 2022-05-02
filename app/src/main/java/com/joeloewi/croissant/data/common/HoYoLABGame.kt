package com.joeloewi.croissant.data.common

import androidx.annotation.StringRes
import com.joeloewi.croissant.R

enum class HoYoLABGame(
    val gameId: Int,
    @StringRes val gameNameResourceId: Int,
    val gameIconUrl: String,
    val redemptionCodesUrl: String
) {
    HonkaiImpact3rd(
        gameId = 1,
        gameNameResourceId = R.string.honkai_impact_3rd_game_name,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/bh3_hoyoverse.png",
        redemptionCodesUrl = "https://arca.live/b/hk3rd/7334792"
    ),
    GenshinImpact(
        gameId = 2,
        gameNameResourceId = R.string.genshin_impact_game_name,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/ys_hoyoverse.png",
        redemptionCodesUrl = "https://arca.live/b/genshin/30819489"
    ),
    TearsOfThemis(
        gameId = 4,
        gameNameResourceId = R.string.tears_of_themis_game_name,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/nxx_hoyoverse.png",
        redemptionCodesUrl = ""
    ),
    Unknown(
        gameId = -1,
        gameNameResourceId = R.string.unknown_game_name,
        gameIconUrl = "",
        redemptionCodesUrl = ""
    );

    companion object {
        fun findByGameId(gameId: Int): HoYoLABGame =
            values().find { it.gameId == gameId } ?: Unknown
    }
}