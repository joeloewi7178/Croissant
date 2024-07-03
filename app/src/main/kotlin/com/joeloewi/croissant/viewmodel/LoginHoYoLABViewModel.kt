package com.joeloewi.croissant.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.webkit.CookieManagerCompat
import androidx.webkit.WebViewFeature
import com.joeloewi.croissant.R
import com.joeloewi.croissant.domain.usecase.SystemUseCase
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginHoYoLABViewModel @Inject constructor(
    private val removeAllCookiesUseCase: SystemUseCase.RemoveAllCookies,
    private val packageManager: PackageManager
) : ViewModel(), ContainerHost<LoginHoYoLABViewModel.State, LoginHoYoLABViewModel.SideEffect> {
    private val _removeAllCookies = flow {
        emit(removeAllCookiesUseCase().foldAsLce())
    }.catch { }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LCE.Loading
    )

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            _removeAllCookies.collect {
                reduce { state.copy(isRemovingCookies = it is LCE.Content) }
            }
        }
    }

    private suspend fun checkCurrentCookieIsValid(
        cookie: String,
        names: List<String>
    ): Boolean = withContext(Dispatchers.IO) { names.map { cookie.contains(it) }.all { it } }

    private suspend fun getCurrentCookie(
        cookieManager: CookieManager,
        url: String,
        baseUrl: String = url
    ): String = withContext(Dispatchers.IO) {
        cookieManager.flush()

        if (WebViewFeature.isFeatureSupported(WebViewFeature.GET_COOKIE_INFO)) {
            val cookies = CookieManagerCompat.getCookieInfo(
                cookieManager,
                url
            ).map { Cookie.parse(baseUrl.toHttpUrl(), it) }

            buildString {
                cookies.filterNotNull().forEachIndexed { index, cookie ->
                    if (index > 0) append("; ")
                    append(cookie.name).append('=').append(cookie.value)
                }
            }
        } else {
            cookieManager.getCookie(baseUrl) ?: ""
        }
    }

    fun onCheckCookie(isManualCheck: Boolean) = intent {
        val currentCookie = getCurrentCookie(
            cookieManager = state.cookieManager,
            url = state.hoyolabUrl
        )

        if (checkCurrentCookieIsValid(currentCookie, state.necessaryKeys)) {
            postSideEffect(SideEffect.NavigateUp(currentCookie))
            return@intent
        }

        if (isManualCheck) {
            postSideEffect(SideEffect.ShowIncorrectCookieSnackbar(R.string.incorrect_session))
        }
    }

    fun onLaunchUri(uri: Uri) = intent {
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (intent.resolveActivity(packageManager) != null) {
            postSideEffect(SideEffect.LaunchIntent(intent))
        }
    }

    fun onShowSslErrorDialog(showSslErrorDialog: Pair<SslErrorHandler?, SslError?>? = null) =
        intent {
            reduce { state.copy(showSslErrorDialog = showSslErrorDialog) }
        }

    data class State(
        val cookieManager: CookieManager = CookieManager.getInstance().apply {
            setAcceptCookie(true)
        },
        val hoyolabUrl: String = "https://m.hoyolab.com",
        val isRemovingCookies: Boolean = true,
        val necessaryKeys: ImmutableList<String> = persistentListOf("ltoken_v2", "ltmid_v2"),
        val excludedUrls: ImmutableList<String> = persistentListOf(
            "www.webstatic-sea.mihoyo.com",
            "www.webstatic-sea.hoyolab.com"
        ),
        val securityPopUpUrls: ImmutableList<String> = persistentListOf(
            "https://account.hoyolab.com/security.html",
            "https://m.hoyolab.com/account-system-sea/security.html",
            "about:blank",
            "https://account.hoyolab.com/single-page/cross-login.html"
        ),
        val showSslErrorDialog: Pair<SslErrorHandler?, SslError?>? = null
    )

    sealed class SideEffect {
        data class NavigateUp(val cookie: String? = null) : SideEffect()
        data class LaunchIntent(val intent: Intent) : SideEffect()
        data class ShowIncorrectCookieSnackbar(
            @StringRes val stringResId: Int
        ) : SideEffect()
    }
}