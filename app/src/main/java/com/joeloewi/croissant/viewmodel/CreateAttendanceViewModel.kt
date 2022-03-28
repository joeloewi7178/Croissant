package com.joeloewi.croissant.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.remote.dao.HoYoLABService
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ObsoleteCoroutinesApi
@HiltViewModel
class CreateAttendanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hoYoLABService: HoYoLABService
) : ViewModel() {
    private val _cookie = MutableStateFlow("")
    private val _hourOfDay = MutableStateFlow(Calendar.getInstance()[Calendar.HOUR_OF_DAY])
    private val _minute = MutableStateFlow(Calendar.getInstance()[Calendar.MINUTE])

    val cookie = _cookie
    val userInfo = _cookie
        .filter { it.isNotEmpty() }
        .map { cookie ->
            hoYoLABService.runCatching {
                getUserFullInfo(cookie).data!!.userInfo
            }.fold(
                onSuccess = {
                    Lce.Content(it)
                },
                onFailure = {
                    Lce.Error(it)
                }
            )
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Lce.Loading
        )
    val connectedGames = userInfo
        .filter { it is Lce.Content }
        .combine(_cookie) { userInfo, cookie ->
            userInfo to cookie
        }.map { pair ->
            hoYoLABService.runCatching {
                getGameRecordCard(
                    pair.second,
                    pair.first.content!!.uid
                ).data!!
            }.fold(
                onSuccess = {
                    val games = it.list.map { gameRecord ->
                        gameRecord.copy(
                            hoYoLABGame = HoYoLABGame.values()
                                .find { hoYoLABGame -> hoYoLABGame.gameId == gameRecord.gameId }
                                ?: HoYoLABGame.Unknown
                        )
                    }
                    Lce.Content(games)
                },
                onFailure = {
                    Lce.Error(it)
                }
            )
        }.flowOn(Dispatchers.IO).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Lce.Loading
        )
    val checkedGames = mutableStateMapOf<HoYoLABGame, Boolean>()
    val tickerCalendar = ticker(delayMillis = 1000).receiveAsFlow().map { Calendar.getInstance() }
    val hourOfDay = _hourOfDay.asStateFlow()
    val minute = _minute.asStateFlow()

    fun setCookie(cookie: String) {
        viewModelScope.launch {
            _cookie.emit(cookie)
        }
    }

    fun setHourOfDay(hourOfDay: Int) {
        _hourOfDay.value = hourOfDay
    }

    fun setMinute(minute: Int) {
        _minute.value = minute
    }
}