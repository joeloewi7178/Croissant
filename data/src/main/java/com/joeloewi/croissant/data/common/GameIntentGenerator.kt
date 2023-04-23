package com.joeloewi.croissant.data.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import com.joeloewi.croissant.domain.common.HoYoLABGame

fun generateGameIntent(
    context: Context,
    hoYoLABGame: HoYoLABGame,
    region: String
): Intent = when (hoYoLABGame) {
    HoYoLABGame.HonkaiImpact3rd -> {
        with(HonkaiImpact3rdServer.findByRegion(region = region)) {
            packageName to fallbackUri
        }
    }

    HoYoLABGame.GenshinImpact -> {
        with(GenshinImpactServer.findByRegion(region = region)) {
            packageName to fallbackUri
        }
    }

    HoYoLABGame.TearsOfThemis -> {
        "com.miHoYo.tot.glb" to "market://details?id=com.miHoYo.tot.glb".toUri()
    }

    HoYoLABGame.Unknown -> {
        "" to Uri.EMPTY
    }
}.let {
    context.packageManager.getLaunchIntentForPackage(it.first)
        ?: if (it.second.authority?.contains("taptap.io") == true) {
            //for chinese server
            Intent(Intent.ACTION_VIEW, it.second)
        } else {
            Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                data = it.second
            }
        }
}