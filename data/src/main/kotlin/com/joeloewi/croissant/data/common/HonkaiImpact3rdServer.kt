package com.joeloewi.croissant.data.common

import android.net.Uri
import androidx.core.net.toUri

enum class HonkaiImpact3rdServer(
    val region: String = "",
    val packageName: String = "",
    //if app does not exist in device
    val fallbackUri: Uri = "market://details?id=${packageName}".toUri()
) {
    CNServer(
        region = "cn01",
        packageName = "com.miHoYo.enterprise.NGHSoD",
        fallbackUri = "https://www.taptap.io/app/10056".toUri()
    ),
    KRServer(
        region = "kr01",
        packageName = "com.miHoYo.bh3korea",
    ),
    JPServer(
        region = "jp01",
        packageName = "com.miHoYo.bh3rdJP"
    ),
    TraditionalChineseServer(
        region = "asia01",
        packageName = "com.miHoYo.bh3tw"
    ),
    SEAServer(
        region = "overseas01",
        packageName = "com.miHoYo.bh3oversea"
    ),
    EuropeServer(
        region = "eur01",
        packageName = "com.miHoYo.bh3global"
    ),
    AmericasServer(
        region = "usa01",
        packageName = "com.miHoYo.bh3global"
    ),

    //cn server's region code is not figured out
    Unknown(
        packageName = "com.miHoYo.enterprise.NGHSoD",
        fallbackUri = "https://www.taptap.io/app/10056".toUri()
    );

    companion object {
        fun findByRegion(region: String) = entries.find { it.region == region } ?: Unknown
    }
}