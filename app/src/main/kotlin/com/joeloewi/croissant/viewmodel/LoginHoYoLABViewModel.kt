package com.joeloewi.croissant.viewmodel

import android.webkit.CookieManager
import android.webkit.ValueCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginHoYoLABViewModel @Inject constructor() : ViewModel() {
    private val _currentCookie = MutableStateFlow("")

    val removeAllCookies = callbackFlow<Lce<Boolean>> {
        var valueCallback: ValueCallback<Boolean>? = ValueCallback<Boolean> { hasRemoved ->
            CookieManager.getInstance().flush()
            trySend(Lce.Content(hasRemoved))
        }

        CookieManager.getInstance().runCatching {
            withContext(Dispatchers.Main) {
                removeAllCookies(valueCallback)
            }
        }.onFailure { cause ->
            close(cause)
        }

        awaitClose { valueCallback = null }
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Lce.Loading
    )

    val currentCookie = _currentCookie.asStateFlow()

    fun setCurrentCookie(currentCookie: String) {
        _currentCookie.value = currentCookie
    }
}