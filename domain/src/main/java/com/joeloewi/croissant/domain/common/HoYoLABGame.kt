/*
 *    Copyright 2023. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.domain.common


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