package com.joeloewi.croissant.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModel
import com.joeloewi.croissant.core.data.model.HoYoLABGame
import com.joeloewi.croissant.domain.usecase.ArcaLiveAppUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.util.toAnnotatedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RedemptionCodesViewModel @Inject constructor(
    private val getRedeemCodeUseCase: ArcaLiveAppUseCase.GetRedeemCode,
    private val packageManager: PackageManager
) : ViewModel(),
    ContainerHost<RedemptionCodesViewModel.State, RedemptionCodesViewModel.SideEffect> {

    override val container: Container<State, SideEffect> = container(State()) {
        getRedemptionCodes()
    }

    fun getRedemptionCodes() = intent {
        reduce { state.copy(redemptionCodes = LCE.Loading) }
        val newState = HoYoLABGame.entries.filter {
            it !in listOf(HoYoLABGame.Unknown, HoYoLABGame.TearsOfThemis)
        }.runCatching {
            map {
                it to HtmlCompat.fromHtml(
                    getRedeemCodeUseCase(it).getOrThrow(),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toAnnotatedString()
            }.toImmutableList()
        }.foldAsLce()
        reduce { state.copy(redemptionCodes = newState) }
    }

    @OptIn(ExperimentalTextApi::class)
    fun onClickUrl(annotatedString: AnnotatedString, offset: Int) = intent {
        annotatedString.getUrlAnnotations(offset, offset).firstOrNull()?.let {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item.url))

            if (intent.resolveActivity(packageManager) != null) {
                postSideEffect(SideEffect.LaunchIntent(intent))
            }
        }
    }

    fun onClickExpand(hoYoLABGame: HoYoLABGame) = intent {
        val expandedItems = state.expandedItems

        withContext(Dispatchers.Main) {
            if (hoYoLABGame in expandedItems) {
                expandedItems.remove(hoYoLABGame)
            } else {
                expandedItems.add(hoYoLABGame)
            }
        }
    }

    data class State(
        val expandedItems: SnapshotStateList<HoYoLABGame> = mutableStateListOf(),
        val redemptionCodes: LCE<ImmutableList<Pair<HoYoLABGame, AnnotatedString>>> = LCE.Loading
    )

    sealed class SideEffect {
        data class LaunchIntent(
            val intent: Intent
        ) : SideEffect()
    }
}