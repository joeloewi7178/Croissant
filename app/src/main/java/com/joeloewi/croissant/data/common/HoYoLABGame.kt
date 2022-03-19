package com.joeloewi.croissant.data.common

import androidx.annotation.StringRes
import com.joeloewi.croissant.R

enum class HoYoLABGame(
    val gameId: Int,
    @StringRes val gameNameResourceId: Int,
    val gameIconUrl: String
) {
    HonkaiImpact3rd(
        gameId = 1,
        gameNameResourceId = R.string.honkai_impact_3rd_game_name,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/bh3_hoyoverse.png"
    ),
    GenshinImpact(
        gameId = 2,
        gameNameResourceId = R.string.genshin_impact_game_name,
        gameIconUrl = "https://webstatic-sea.hoyolab.com/communityweb/business/ys_hoyoverse.png"
    ),
    Unknown(
        gameId = -1,
        gameNameResourceId = R.string.unknown_game_name,
        gameIconUrl = ""
    );
}