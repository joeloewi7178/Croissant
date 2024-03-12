package com.joeloewi.croissant.viewmodel

import android.webkit.CookieManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.webkit.CookieManagerCompat
import androidx.webkit.WebViewFeature
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsILCE
import com.joeloewi.croissant.state.foldAsLce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

@HiltViewModel
class LoginHoYoLABViewModel @Inject constructor(
    private val removeAllCookiesUseCase: SystemUseCase.RemoveAllCookies,
) : ViewModel() {
    private val _cookieManager = CookieManager.getInstance()
    private val _hoyolabUrl = "https://m.hoyolab.com"
    private val _checkCookieManuallyChannel = Channel<Boolean>(Channel.BUFFERED)
    private val _checkCookieManually = _checkCookieManuallyChannel.receiveAsFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )
    private val _cookieNames = listOf("ltoken_v2", "ltmid_v2")
    private val _mutex = Mutex()

    val hoyolabUrl = "https://m.hoyolab.com"
    val removeAllCookies = flow {
        emit(removeAllCookiesUseCase().foldAsLce())
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LCE.Loading
    )
    val manuallyCheckedCookie = _checkCookieManually.filter { it }.flatMapLatest {
        flow {
            emit(ILCE.Loading)

            emit(runCatching {
                with(getCurrentCookie(_cookieManager, _hoyolabUrl)) {
                    checkCurrentCookieIsValid(
                        cookie = this,
                        names = _cookieNames
                    ) to this
                }
            }.foldAsILCE())
        }
    }.catch { }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ILCE.Idle
        )
    val automaticallyCheckedCookie = _checkCookieManually.filter { !it }.map {
        getCurrentCookie(_cookieManager, _hoyolabUrl)
    }.filter { cookie ->
        checkCurrentCookieIsValid(
            cookie,
            _cookieNames
        )
    }
        .catch { }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    private fun checkCurrentCookieIsValid(
        cookie: String,
        names: List<String>
    ): Boolean = names.map { cookie.contains(it) }.all { it }

    private fun getCurrentCookie(
        cookieManager: CookieManager,
        url: String
    ): String {
        cookieManager.flush()

        return if (WebViewFeature.isFeatureSupported(WebViewFeature.GET_COOKIE_INFO)) {
            val cookies = CookieManagerCompat.getCookieInfo(
                cookieManager,
                url
            ).map { Cookie.parse(_hoyolabUrl.toHttpUrl(), it) }

            buildString {
                cookies.filterNotNull().forEachIndexed { index, cookie ->
                    if (index > 0) append("; ")
                    append(cookie.name).append('=').append(cookie.value)
                }
            }
        } else {
            cookieManager.getCookie(_hoyolabUrl) ?: ""
        }
    }

    fun onCheckCookie(checkManually: Boolean) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
            _mutex.withLock {
                _checkCookieManuallyChannel.send(checkManually)
            }
        }
    }
}