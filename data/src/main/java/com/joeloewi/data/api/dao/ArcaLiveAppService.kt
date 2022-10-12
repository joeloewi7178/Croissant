package com.joeloewi.data.api.dao

import com.joeloewi.data.api.model.response.ArticleResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ArcaLiveAppService {
    @GET("view/article/{slug}/{articleId}")
    suspend fun getArticle(
        @Header("User-Agent") userAgent: String = "live.arca.android.playstore/0.8.331-playstore",
        @Path("slug") slug: String,
        @Path("articleId") articleId: Long,
        @Query("viewCount") viewCount: Boolean = false
    ): ApiResponse<ArticleResponse>
}