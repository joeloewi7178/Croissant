package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginHoYoLABScreen(
    loginHoYoLABViewModel: LoginHoYoLABViewModel = hiltViewModel(),
    onNavigateUp: (cookie: String?) -> Unit
) {
    val state by loginHoYoLABViewModel.collectAsState()
    val activity = LocalActivity.current
    val webViewState = rememberWebViewState(url = state.hoyolabUrl)
    val webViewNavigator = rememberWebViewNavigator()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    loginHoYoLABViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginHoYoLABViewModel.SideEffect.NavigateUp -> {
                onNavigateUp(sideEffect.cookie)
            }

            is LoginHoYoLABViewModel.SideEffect.LaunchIntent -> {
                activity.startActivity(sideEffect.intent)
            }

            is LoginHoYoLABViewModel.SideEffect.ShowIncorrectCookieSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        activity.getString(sideEffect.stringResId)
                    )
                }
            }
        }
    }

    LoginHoYoLABContent(
        state = state,
        snackbarHostState = snackbarHostState,
        webViewState = webViewState,
        webViewNavigator = webViewNavigator,
        onNavigateUp = onNavigateUp,
        onCheckCookie = loginHoYoLABViewModel::onCheckCookie,
        onShowSSLErrorDialog = loginHoYoLABViewModel::onShowSslErrorDialog,
        onLaunchUri = loginHoYoLABViewModel::onLaunchUri
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginHoYoLABContent(
    state: LoginHoYoLABViewModel.State,
    snackbarHostState: SnackbarHostState,
    webViewState: WebViewState,
    webViewNavigator: WebViewNavigator,
    onNavigateUp: (cookie: String?) -> Unit,
    onCheckCookie: (Boolean) -> Unit,
    onShowSSLErrorDialog: (Pair<SslErrorHandler?, SslError?>?) -> Unit,
    onLaunchUri: (Uri) -> Unit
) {
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { onNavigateUp(null) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = Icons.Default.Close.name
                            )
                        }
                    },
                    actions = {
                        ReloadOrStopLoading(
                            isLoading = webViewState.isLoading,
                            reload = {
                                IconButton(
                                    onClick = {
                                        webViewNavigator.reload()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = Icons.Default.Refresh.name
                                    )
                                }
                            },
                            stopLoading = {
                                IconButton(
                                    onClick = {
                                        webViewNavigator.stopLoading()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = Icons.Default.Close.name
                                    )
                                }
                            },
                        )

                        IconButton(
                            onClick = { onCheckCookie(true) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = Icons.Default.Done.name
                            )
                        }
                    },
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Text(
                                text = webViewState.pageTitle ?: "Title",
                                style = MaterialTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            if (!webViewState.lastLoadedUrl.isNullOrEmpty()) {
                                Text(
                                    text = webViewState.lastLoadedUrl ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                )
                with(webViewState.loadingState) {
                    when (this) {
                        is LoadingState.Loading -> {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                progress = { progress }
                            )
                        }

                        LoadingState.Finished -> {

                        }

                        LoadingState.Initializing -> {

                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        if (state.isRemovingCookies) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {}
        } else {
            WebView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                state = webViewState,
                navigator = webViewNavigator,
                onCreated = { webView ->
                    with(webView) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
                        }
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            setSupportMultipleWindows(true)
                            javaScriptCanOpenWindowsAutomatically = true
                            userAgentString = userAgentString.replace("; wv", "")
                        }

                        runCatching {
                            if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                                    settings,
                                    true
                                )
                            }

                            if (WebViewFeature.isFeatureSupported(WebViewFeature.ENTERPRISE_AUTHENTICATION_APP_LINK_POLICY)) {
                                WebSettingsCompat.setEnterpriseAuthenticationAppLinkPolicyEnabled(
                                    settings,
                                    true
                                )
                            }
                        }

                        clearCache(true)
                        clearFormData()
                        clearHistory()
                        clearMatches()
                        clearSslPreferences()
                    }

                    WebStorage.getInstance().deleteAllData()

                    state.cookieManager.setAcceptThirdPartyCookies(webView, true)
                },
                onDispose = {
                    it.destroy()
                },
                client = remember {
                    object : AccompanistWebViewClient() {
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            super.onReceivedSslError(view, handler, error)
                            onShowSSLErrorDialog(handler to error)
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            onCheckCookie(false)

                            return super.shouldInterceptRequest(
                                view,
                                request
                            )
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean =
                            if ((mutableListOf(state.hoyolabUrl) + state.excludedUrls).all {
                                    (request?.url?.toString()?.contains(it) == false)
                                }) {
                                request?.url?.let { onLaunchUri(it) }
                                true
                            } else {
                                super.shouldOverrideUrlLoading(view, request)
                            }
                    }
                },
                chromeClient = remember {
                    object : AccompanistWebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean =
                            if (BuildConfig.DEBUG) {
                                super.onConsoleMessage(consoleMessage)
                            } else {
                                true
                            }

                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?
                        ): Boolean {
                            val popUpWebView = WebView(activity).apply {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
                                }
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    setSupportMultipleWindows(true)
                                    javaScriptCanOpenWindowsAutomatically = true
                                    userAgentString = userAgentString.replace("; wv", "")
                                }

                                runCatching {
                                    if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                                        WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                                            settings,
                                            true
                                        )
                                    }
                                }
                            }

                            state.cookieManager.setAcceptThirdPartyCookies(popUpWebView, true)

                            val dialog = Dialog(activity).apply {
                                setContentView(popUpWebView)
                            }

                            dialog.window?.run {
                                WindowCompat.setDecorFitsSystemWindows(this, true)

                                attributes = attributes?.apply {
                                    width = ViewGroup.LayoutParams.MATCH_PARENT
                                    height = ViewGroup.LayoutParams.MATCH_PARENT
                                }
                            }

                            dialog.apply {
                                setOnDismissListener {
                                    popUpWebView.destroy()
                                }
                            }

                            popUpWebView.apply {
                                webChromeClient = object : WebChromeClient() {
                                    override fun onCloseWindow(window: WebView?) {
                                        dialog.dismiss()
                                    }
                                }
                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(
                                        view: WebView?,
                                        url: String?,
                                        favicon: Bitmap?
                                    ) {
                                        if (state.securityPopUpUrls.any { url?.contains(it) == true }) {
                                            dialog.dismiss()
                                        } else {
                                            dialog.show()
                                        }
                                        super.onPageStarted(view, url, favicon)
                                    }
                                }
                            }

                            resultMsg?.run {
                                (this.obj as WebView.WebViewTransport).webView =
                                    popUpWebView
                                sendToTarget()
                            }

                            return true
                        }
                    }
                }
            )
        }
    }

    if (state.showSslErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { onShowSSLErrorDialog(null) },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
            confirmButton = {
                TextButton(
                    onClick = { state.showSslErrorDialog.first?.proceed() }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { state.showSslErrorDialog.first?.cancel() }
                ) {
                    Text(text = stringResource(id = R.string.dismiss))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = Icons.Default.Warning.name
                )
            },
            title = {
                Text(text = stringResource(id = R.string.certification_error))
            },
            text = {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(id = R.string.website_user_try_to_access))
                        state.showSslErrorDialog.second?.url?.let {
                            append("(${it})")
                        }
                        append(stringResource(id = R.string.has_error_in_certification))
                    },
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@Composable
fun ReloadOrStopLoading(
    isLoading: Boolean,
    reload: @Composable () -> Unit,
    stopLoading: @Composable () -> Unit
) {
    if (!isLoading) {
        reload()
    } else {
        stopLoading()
    }
}