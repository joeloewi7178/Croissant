package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hoYoLABService: HoYoLABService
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _connectedGames: StateFlow<Lce<List<GameRecord>>> = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            try {
                val userFullInfo = hoYoLABService.getUserFullInfo(cookie)
                val gameRecordCard = hoYoLABService.getGameRecordCard(
                    cookie,
                    userFullInfo.data!!.userInfo.uid
                )

                Lce.Content(
                    gameRecordCard.data!!.list.map { gameRecord ->
                        gameRecord.copy(
                            hoYoLABGame = HoYoLABGame.values()
                                .find { it.gameId == gameRecord.gameId }
                                ?: HoYoLABGame.Unknown
                        )
                    }
                )
            } catch (cause: Exception) {
                Lce.Error(cause)
            }
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Lce.Loading
        )

    val cookie = _cookie.asStateFlow()
    val connectedGames = _connectedGames
    val checkedGames = SnapshotStateMap<HoYoLABGame, Boolean>()

    fun setCookie(cookie: String) {
        _cookie.value = cookie
    }
}