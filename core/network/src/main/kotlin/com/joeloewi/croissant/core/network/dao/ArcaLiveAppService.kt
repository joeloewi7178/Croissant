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

package com.joeloewi.croissant.core.network.dao

import com.joeloewi.croissant.core.network.model.response.ArticleResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ArcaLiveAppService {
    @GET("view/article/{slug}/{articleId}")
    suspend fun getArticle(
        @Header("User-Agent") userAgent: String = "net.umanle.arca.android.playstore/0.9.57",
        @Path("slug") slug: String,
        @Path("articleId") articleId: Long,
        @Query("viewCount") viewCount: Boolean = false
    ): ApiResponse<ArticleResponse>
}