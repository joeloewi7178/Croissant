package com.joeloewi.croissant.viewmodel

import android.webkit.CookieManager
import android.webkit.ValueCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LoginHoYoLABViewModel @Inject constructor() : ViewModel() {
    val removeAllCookies = callbackFlow<Lce<Boolean>> {
        var valueCallback: ValueCallback<Boolean>? = ValueCallback<Boolean> { hasRemoved ->
            CookieManager.getInstance().flush()
            trySend(Lce.Content(hasRemoved))
        }

        CookieManager.getInstance().runCatching {
            removeAllCookies(valueCallback)
        }.onFailure { cause ->
            close(cause)
        }

        awaitClose { valueCallback = null }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Lce.Loading
    )
}