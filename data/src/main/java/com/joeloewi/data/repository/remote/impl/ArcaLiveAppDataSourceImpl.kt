package com.joeloewi.data.repository.remote.impl

import com.joeloewi.data.api.dao.ArcaLiveAppService
import com.joeloewi.data.api.model.response.ArticleResponse
import com.joeloewi.data.repository.remote.ArcaLiveAppDataSource
import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArcaLiveAppDataSourceImpl @Inject constructor(
    private val arcaLiveAppService: ArcaLiveAppService,
    private val coroutineDispatcher: CoroutineDispatcher,
) : ArcaLiveAppDataSource {
    override suspend fun getArticle(slug: String, articleId: Long): ApiResponse<ArticleResponse> =
        withContext(coroutineDispatcher) {
            arcaLiveAppService.getArticle(
                slug = slug,
                articleId = articleId
            )
        }
}