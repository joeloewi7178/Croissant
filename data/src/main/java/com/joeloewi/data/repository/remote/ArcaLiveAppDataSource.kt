package com.joeloewi.data.repository.remote

import com.joeloewi.data.api.model.response.ArticleResponse
import com.skydoves.sandwich.ApiResponse

interface ArcaLiveAppDataSource {
    suspend fun getArticle(
        slug: String,
        articleId: Long
    ): ApiResponse<ArticleResponse>
}