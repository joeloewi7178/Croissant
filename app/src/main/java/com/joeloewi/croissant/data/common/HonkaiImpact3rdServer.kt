package com.joeloewi.croissant.data.common

enum class HonkaiImpact3rdServer(
    val region: String = "",
    val packageName: String = "",
) {
    CNServer(
        region = "cn01",
        packageName = "com.miHoYo.enterprise.NGHSoD"
    ),
    KRServer(
        region = "kr01",
        packageName = "com.miHoYo.bh3korea"
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
    Unknown;

    companion object {
        fun findByRegion(region: String): HonkaiImpact3rdServer =
            values().find { it.region == region } ?: Unknown
    }
}