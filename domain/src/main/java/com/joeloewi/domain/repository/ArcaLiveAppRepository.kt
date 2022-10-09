package com.joeloewi.domain.repository

interface ArcaLiveAppRepository {
    suspend fun getArticle(
        slug: String,
        articleId: Long
    ): Result<String>
}