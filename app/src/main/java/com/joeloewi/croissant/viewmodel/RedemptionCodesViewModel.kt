package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.usecase.ArcaLiveAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class RedemptionCodesViewModel @Inject constructor(
    private val getArticleArcaLiveAppUseCase: ArcaLiveAppUseCase.GetArticle
) : ViewModel() {
    private val _hoYoLABGameRedemptionCodesState =
        MutableStateFlow<Lce<List<Pair<HoYoLABGame, String>>>>(Lce.Loading)

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
                            it to (getRedemptionCodesFromHtml(it).getOrThrow())
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
                            select("img").remove()
                            repeat(5) {
                                select("p:last-child").remove()
                            }
                        }.html().replace("모유", "체력")
                    }
                }
                HoYoLABGame.GenshinImpact -> {
                    getArticleArcaLiveAppUseCase(
                        slug = "genshin",
                        articleId = 53699739
                    ).mapCatching { content ->
                        Jsoup.parse(content).select("p:nth-child(56)").html()
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