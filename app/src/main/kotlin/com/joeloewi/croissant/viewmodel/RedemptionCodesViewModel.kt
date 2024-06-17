package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.domain.usecase.ArcaLiveAppUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.util.toAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RedemptionCodesViewModel @Inject constructor(
    private val getRedeemCodeUseCase: ArcaLiveAppUseCase.GetRedeemCode
) : ViewModel() {
    private val _hoYoLABGameRedemptionCodesState =
        MutableStateFlow<LCE<ImmutableList<Pair<HoYoLABGame, AnnotatedString>>>>(LCE.Loading)

    val hoYoLABGameRedemptionCodesState = _hoYoLABGameRedemptionCodesState.asStateFlow()
    val expandedItems = mutableStateListOf<HoYoLABGame>()

    init {
        getRedemptionCodes()
    }

    fun getRedemptionCodes() {
        _hoYoLABGameRedemptionCodesState.value = LCE.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _hoYoLABGameRedemptionCodesState.value = HoYoLABGame.entries.filter {
                it !in listOf(HoYoLABGame.Unknown, HoYoLABGame.TearsOfThemis)
            }.runCatching {
                map {
                    async(SupervisorJob() + Dispatchers.IO) {
                        it to HtmlCompat.fromHtml(
                            getRedeemCodeUseCase(it).getOrThrow(),
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        ).toAnnotatedString()
                    }
                }.awaitAll().toImmutableList()
            }.foldAsLce()
        }
    }
}