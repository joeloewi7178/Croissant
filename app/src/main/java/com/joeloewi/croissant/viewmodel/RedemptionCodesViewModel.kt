package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import com.joeloewi.domain.common.HoYoLABGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class RedemptionCodesViewModel @Inject constructor() : ViewModel() {
    private val _userAgent = "Chrome"
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
                    map { it to getRedemptionCodesFromHtml(it) }
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

    private suspend fun getRedemptionCodesFromHtml(hoYoLABGame: HoYoLABGame): String =
        withContext(Dispatchers.IO) {
            when (hoYoLABGame) {
                HoYoLABGame.HonkaiImpact3rd -> {
                    Jsoup.connect(hoYoLABGame.redemptionCodesUrl)
                        .userAgent(_userAgent)
                        .get()
                        .getElementsByClass("article-content")[0]
                        .apply {
                            select("img").remove()
                            select("p:last-child").remove()
                            select("p:last-child").remove()
                            select("p:last-child").remove()
                            select("p:last-child").remove()
                            select("p:last-child").remove()
                        }.html().replace("모유", "체력")
                }
                HoYoLABGame.GenshinImpact -> {
                    val articleContent =
                        Jsoup.connect(hoYoLABGame.redemptionCodesUrl)
                            .userAgent(_userAgent)
                            .get()
                            .getElementsByClass("article-content")[0]

                    var htmlStrings = ""
                    var siblingIndex = 0

                    //between fifth hr and sixth hr
                    while (true) {
                        val element =
                            articleContent.select("hr")[5].nextElementSiblings()[siblingIndex++]

                        if (element.tagName() == "hr") {
                            break
                        } else {
                            htmlStrings += element.html()
                        }
                    }

                    htmlStrings
                }
                else -> {
                    ""
                }
            }
        }
}