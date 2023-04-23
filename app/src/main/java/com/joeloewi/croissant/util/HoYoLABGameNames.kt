package com.joeloewi.croissant.util

import androidx.annotation.StringRes
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.common.HoYoLABGame

@StringRes
fun HoYoLABGame.gameNameStringResId(): Int = when (this) {
    HoYoLABGame.HonkaiImpact3rd -> {
        R.string.honkai_impact_3rd_game_name
    }

    HoYoLABGame.GenshinImpact -> {
        R.string.genshin_impact_game_name
    }

    HoYoLABGame.TearsOfThemis -> {
        R.string.tears_of_themis_game_name
    }

    HoYoLABGame.Unknown -> {
        R.string.unknown_game_name
    }
}