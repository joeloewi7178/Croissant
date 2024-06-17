package com.joeloewi.croissant.core.common

import android.net.Uri
import androidx.core.net.toUri

enum class GenshinImpactServer(
    val region: String = "",
    val packageName: String = "com.miHoYo.GenshinImpact",
    //if app does not exist in device
    val fallbackUri: Uri = "market://details?id=${packageName}".toUri()
) {
    CNServer(
        fallbackUri = "https://www.taptap.io/app/191001".toUri()
    ),
    AsiaServer(
        region = "os_asia",
    ),
    EuropeServer(
        region = "os_euro",
    ),
    AmericasServer(
        region = "os_usa",
    ),
    TraditionalChineseServer(
        region = "os_cht"
    ),

    //cn server's region code is not figured out
    Unknown(
        fallbackUri = "https://www.taptap.io/app/191001".toUri()
    );

    companion object {
        fun findByRegion(region: String) = entries.find { it.region == region } ?: Unknown
    }
}