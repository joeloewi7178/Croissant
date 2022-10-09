package com.joeloewi.data.repository

import com.joeloewi.data.repository.remote.ArcaLiveAppDataSource
import com.joeloewi.domain.repository.ArcaLiveAppRepository
import com.skydoves.sandwich.getOrThrow
import javax.inject.Inject

class ArcaLiveAppRepositoryImpl @Inject constructor(
    private val arcaLiveAppDataSource: ArcaLiveAppDataSource
): ArcaLiveAppRepository {
    override suspend fun getArticle(slug: String, articleId: Long): Result<String> =
        arcaLiveAppDataSource.runCatching {
            getArticle(slug, articleId).getOrThrow()
        }.mapCatching { articleResponse ->
            articleResponse.content
        }
}