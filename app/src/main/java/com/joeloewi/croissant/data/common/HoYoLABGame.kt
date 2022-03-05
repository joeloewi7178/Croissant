package com.joeloewi.croissant.data.common

enum class HoYoLABGame {
    //from hoyolab
    //honkai impact 3rd game id = 1
    //genshin impact game id = 2

    //In this project, uses enum class's ordinal and values()[index] to achieve converting between gameId and enum class
    //ex) convert id to enum class
    //HoYoLABGame.values()[gameId - 1]
    //--> maybe causes exception
    //
    //ex) convert enum class to id
    //val gameId = HoYoLAB.HonkaiImpact3rd.ordinal + 1

    HonkaiImpact3rd,
    GenshinImpact,
    Unknown;
}