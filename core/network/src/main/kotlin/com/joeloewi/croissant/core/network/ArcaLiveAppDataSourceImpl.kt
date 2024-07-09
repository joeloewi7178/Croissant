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

package com.joeloewi.croissant.core.network

import com.joeloewi.croissant.core.model.DataHoYoLABGame
import com.joeloewi.croissant.core.network.dao.ArcaLiveAppService
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.mapSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

class ArcaLiveAppDataSourceImpl @Inject constructor(
    private val arcaLiveAppService: ArcaLiveAppService,
) : ArcaLiveAppDataSource {

    override suspend fun getRedeemCode(
        game: DataHoYoLABGame
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            when (game) {
                DataHoYoLABGame.HonkaiImpact3rd -> {
                    arcaLiveAppService.getArticle(
                        slug = "hk3rd",
                        articleId = 85815048
                    ).mapSuccess {
                        Jsoup.parse(content).apply {
                            select("*:has(> img)").remove()
                            repeat(5) {
                                select("body > p:last-child").remove()
                            }
                        }.html()
                    }
                }

                DataHoYoLABGame.GenshinImpact -> {
                    arcaLiveAppService.getArticle(
                        slug = "genshin",
                        articleId = 95519559
                    ).mapSuccess {
                        Jsoup.parse(content).apply {
                            select("img").remove()
                        }.select("table:first-of-type").apply {
                            select("tr:last-child").remove()
                        }.html().replace("https://oo.pe/", "")
                    }
                }

                DataHoYoLABGame.HonkaiStarRail -> {
                    arcaLiveAppService.getArticle(
                        slug = "hkstarrail",
                        articleId = 72618649
                    ).mapSuccess {
                        Jsoup.parse(content)
                            .apply { select("img").remove() }
                            .select("td:first-of-type")
                            .html()
                            .replace("https://oo.pe/", "")
                    }
                }

                else -> {
                    arcaLiveAppService.getArticle(
                        slug = "zenlesszonezero",
                        articleId = 109976603
                    ).mapSuccess {
                        Jsoup.parse(content)
                            .apply { select("img").remove() }
                            .html()
                            .replace("https://oo.pe/", "")
                    }
                }
            }
        }
    }
}