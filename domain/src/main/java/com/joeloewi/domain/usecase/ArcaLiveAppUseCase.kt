package com.joeloewi.domain.usecase

import com.joeloewi.domain.repository.ArcaLiveAppRepository
import javax.inject.Inject

sealed class ArcaLiveAppUseCase {
    class GetArticle @Inject constructor(
        private val arcaLiveAppRepository: ArcaLiveAppRepository
    ) {
        suspend operator fun invoke(
            slug: String,
            articleId: Long
        ) = arcaLiveAppRepository.getArticle(slug, articleId)
    }
}