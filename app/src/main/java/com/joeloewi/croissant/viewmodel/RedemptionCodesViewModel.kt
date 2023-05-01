package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.domain.common.HoYoLABGame
import com.joeloewi.croissant.domain.usecase.ArcaLiveAppUseCase
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.util.toAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class RedemptionCodesViewModel @Inject constructor(
    private val getArticleArcaLiveAppUseCase: ArcaLiveAppUseCase.GetArticle
) : ViewModel() {
    private val _hoYoLABGameRedemptionCodesState =
        MutableStateFlow<Lce<List<Pair<HoYoLABGame, AnnotatedString>>>>(Lce.Loading)

    val hoYoLABGameRedemptionCodesState = _hoYoLABGameRedemptionCodesState.asStateFlow()
    val expandedItems = mutableStateListOf<HoYoLABGame>()

    init {
        getRedemptionCodes()
    }

    fun getRedemptionCodes() {
        _hoYoLABGameRedemptionCodesState.update { Lce.Loading }
        viewModelScope.launch(Dispatchers.IO) {
            _hoYoLABGameRedemptionCodesState.update {
                HoYoLABGame.values().runCatching {
                    map {
                        async(SupervisorJob() + Dispatchers.IO) {
                            it to HtmlCompat.fromHtml(
                                getRedemptionCodesFromHtml(it).getOrThrow(),
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            ).toAnnotatedString()
                        }
                    }
                }.mapCatching {
                    it.awaitAll()
                }.fold(
                    onSuccess = {
                        Lce.Content(it)
                    },
                    onFailure = {
                        Lce.Error(it)
                    }
                )
            }
        }
    }

    private suspend fun getRedemptionCodesFromHtml(hoYoLABGame: HoYoLABGame): Result<String> =
        withContext(Dispatchers.IO) {
            //Jsoup's nth-child works differently than expected
            when (hoYoLABGame) {
                HoYoLABGame.HonkaiImpact3rd -> {
                    getArticleArcaLiveAppUseCase(
                        slug = "hk3rd",
                        articleId = 7334792
                    ).mapCatching { content ->
                        Jsoup.parse(content).apply {
                            select("*:has(> img)").remove()
                            repeat(5) {
                                select("body > p:last-child").remove()
                            }
                        }.html().replace("모유", "체력")
                    }
                }

                HoYoLABGame.GenshinImpact -> {
                    getArticleArcaLiveAppUseCase(
                        slug = "genshin",
                        articleId = 67138915
                    ).mapCatching { content ->
                        Jsoup.parse(content).apply {
                            repeat(2) {
                                select("p:last-child").remove()
                            }
                        }.select("p:nth-child(n+8)").html().replace("https://oo.pe/", "")
                    }
                }

                HoYoLABGame.HonkaiStarRail -> {
                    getArticleArcaLiveAppUseCase(
                        slug = "hkstarrail",
                        articleId = 69911111
                    ).mapCatching { content ->
                        Jsoup.parse(content).apply {
                            repeat(9) {
                                select("p:last-child").remove()
                            }
                        }.select("p:nth-child(n+48)").html().replace("https://oo.pe/", "")
                    }
                }

                else -> {
                    runCatching {
                        ""
                    }
                }
            }
        }
}