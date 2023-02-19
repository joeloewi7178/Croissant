package com.joeloewi.croissant.domain.repository

interface ArcaLiveAppRepository {
    suspend fun getArticle(
        slug: String,
        articleId: Long
    ): Result<String>
}