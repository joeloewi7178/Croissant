package com.joeloewi.croissant.state

import android.net.Uri
import android.net.http.SslError
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewState
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.COOKIE
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class LoginHoYoLABState(
    private val navController: NavHostController,
    private val loginHoYoLABViewModel: LoginHoYoLABViewModel,
    val hoyolabUrl: String,
    private val excludedUrls: ImmutableList<String>,
    val securityPopUpUrls: ImmutableList<String>,
    val webViewState: WebViewState,
    val snackbarHostState: SnackbarHostState,
    val webViewNavigator: WebViewNavigator,
    val coroutineScope: CoroutineScope,
) {
    val removeAllCookiesState
        @Composable get() = loginHoYoLABViewModel.removeAllCookies.collectAsStateWithLifecycle().value
    var showSslErrorDialog by mutableStateOf<Pair<SslErrorHandler?, SslError?>?>(null)
        private set
    val pageTitle
        @Composable get() = webViewState.pageTitle ?: "Title"
    val currentUrl
        @Composable get() = webViewState.content.getCurrentUrl() ?: "URL"
    private var navigateUpJob by mutableStateOf<Job?>(null)

    fun onClickClose() {
        navController.navigateUp()
    }

    fun onConfirmSslErrorDialog() {
        showSslErrorDialog?.first?.proceed()
        showSslErrorDialog = null
    }

    fun onCancelSslErrorDialog() {
        showSslErrorDialog?.first?.cancel()
        showSslErrorDialog = null
    }

    fun onShowSslErrorDialogChange(handlerToError: Pair<SslErrorHandler?, SslError?>?) {
        showSslErrorDialog = handlerToError
    }

    fun tryToCatchCookie(failureMessage: String) {
        val currentCookie =
            CookieManager.getInstance().getCookie(hoyolabUrl)

        if (currentCookie.checkContainsHoYoLABCookies()) {
            setToSavedStateAndNavigateUp(currentCookie)
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = failureMessage
                )
            }
        }
    }

    fun checkAndCatchCookieOnlyOnce() {
        val currentCookie =
            CookieManager.getInstance().getCookie(hoyolabUrl)

        if (currentCookie.checkContainsHoYoLABCookies() && navigateUpJob == null) {
            navigateUpJob = coroutineScope.launch(Dispatchers.Main) {
                setToSavedStateAndNavigateUp(currentCookie)
            }
        }
    }

    fun shouldOverrideUrlLoading(
        request: WebResourceRequest?,
        runOuterApplication: (Uri?) -> Unit,
        processOnWebView: () -> Boolean
    ): Boolean = if (shouldShowViaOuterApplication(request)) {
        coroutineScope.launch {
            runOuterApplication(request?.url)
        }
        true
    } else {
        processOnWebView()
    }

    //parts of url, not exactly same
    //list(A,B,C)
    /*
    url         contains(A) contains(B) contains(C)     all(true)   *all(false) any(true)   any(false)
    A/X/Y/Z     true        false       false           false       false       true        true
    ...
    D/...       false       false       false           false       true        false       true
    */
    private fun shouldShowViaOuterApplication(
        request: WebResourceRequest?
    ): Boolean = (mutableListOf(hoyolabUrl) + excludedUrls).all {
        (request?.url?.toString()?.contains(it) == false)
    }

    private fun String?.checkContainsHoYoLABCookies(): Boolean =
        if (isNullOrEmpty()) {
            false
        } else {
            listOf("ltoken", "cookie_token").map { contains(it) }.all { it }
        }

    private fun setToSavedStateAndNavigateUp(
        currentCookie: String
    ) {
        navController.apply {
            previousBackStackEntry?.savedStateHandle?.set(COOKIE, currentCookie)
            navigateUp()
        }
    }
}

@Composable
fun rememberLoginHoYoLABState(
    navController: NavHostController,
    loginHoYoLABViewModel: LoginHoYoLABViewModel,
    hoyolabUrl: String,
    excludedUrls: ImmutableList<String>,
    securityPopUpUrls: ImmutableList<String>,
    webViewState: WebViewState = rememberWebViewState(url = hoyolabUrl),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    webViewNavigator: WebViewNavigator,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(
    navController,
    loginHoYoLABViewModel,
    hoyolabUrl,
    excludedUrls,
    securityPopUpUrls,
    webViewState,
    snackbarHostState,
    webViewNavigator,
    coroutineScope,
) {
    LoginHoYoLABState(
        navController = navController,
        loginHoYoLABViewModel = loginHoYoLABViewModel,
        hoyolabUrl = hoyolabUrl,
        excludedUrls = excludedUrls,
        securityPopUpUrls = securityPopUpUrls,
        webViewState = webViewState,
        snackbarHostState = snackbarHostState,
        webViewNavigator = webViewNavigator,
        coroutineScope = coroutineScope,
    )
}