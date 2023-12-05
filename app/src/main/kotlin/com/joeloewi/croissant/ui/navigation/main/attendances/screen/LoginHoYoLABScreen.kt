package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.*
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.LoginHoYoLABState
import com.joeloewi.croissant.state.rememberLoginHoYoLABState
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.collections.immutable.toImmutableList

const val COOKIE = "cookie"

@Composable
fun LoginHoYoLABScreen(
    navController: NavHostController,
    loginHoYoLABViewModel: LoginHoYoLABViewModel
) {
    val loginHoYoLABState = rememberLoginHoYoLABState(
        navController = navController,
        loginHoYoLABViewModel = loginHoYoLABViewModel,
        hoyolabUrl = remember { "https://m.hoyolab.com" },
        excludedUrls = remember {
            listOf("www.webstatic-sea.mihoyo.com", "www.webstatic-sea.hoyolab.com")
        }.toImmutableList(),
        securityPopUpUrls = remember {
            listOf(
                "https://account.hoyolab.com/security.html",
                "https://m.hoyolab.com/account-system-sea/security.html",
                "about:blank",
                "https://account.hoyolab.com/single-page/cross-login.html"
            )
        }.toImmutableList(),
        webViewNavigator = rememberWebViewNavigator()
    )

    LoginHoYoLABContent(
        loginHoYoLABState = loginHoYoLABState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginHoYoLABContent(
    loginHoYoLABState: LoginHoYoLABState
) {
    val incorrectSession = stringResource(id = R.string.incorrect_session)
    val context = LocalContext.current
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = loginHoYoLABState::onClickClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = Icons.Default.Close.name
                            )
                        }
                    },
                    actions = {
                        ReloadOrStopLoading(
                            isLoading = loginHoYoLABState.webViewState.isLoading,
                            reload = {
                                IconButton(
                                    onClick = {
                                        loginHoYoLABState.webViewNavigator.reload()
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
                                        loginHoYoLABState.webViewNavigator.stopLoading()
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
                            onClick = {
                                loginHoYoLABState.tryToCatchCookie(failureMessage = incorrectSession)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = Icons.Default.Done.name
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = loginHoYoLABState.pageTitle,
                                style = MaterialTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = loginHoYoLABState.currentUrl,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                )
                with(loginHoYoLABState.webViewState.loadingState) {
                    when (this) {
                        is LoadingState.Loading -> {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                progress = progress
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
        snackbarHost = {
            SnackbarHost(hostState = loginHoYoLABState.snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        when (loginHoYoLABState.removeAllCookiesState) {
            is Lce.Content -> {
                WebView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = loginHoYoLABState.webViewState,
                    navigator = loginHoYoLABState.webViewNavigator,
                    onCreated = { webView ->
                        with(webView) {
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

                        CookieManager.getInstance().apply {
                            setAcceptCookie(true)
                            setAcceptThirdPartyCookies(webView, true)
                        }
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
                                loginHoYoLABState.onShowSslErrorDialogChange(handler to error)
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                //in this block, codes are executed in io thread.
                                //checkAndCatchCookieOnlyOnce callback's role is to execute navController.navigateUp()
                                //which is must executed in main thread.
                                //in addition, shouldInterceptRequest() callback is called many times
                                //but navController.navigateUp() has to be called only once

                                //so, after switching context to main thread, store that job in variable
                                //if the variable is null to ensure execute only once
                                loginHoYoLABState.checkAndCatchCookieOnlyOnce()

                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean =
                                loginHoYoLABState.shouldOverrideUrlLoading(
                                    request = request,
                                    runOuterApplication = { uri ->
                                        if (uri != null) {
                                            activity.startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    uri
                                                )
                                            )
                                        }
                                    },
                                    processOnWebView = {
                                        super.shouldOverrideUrlLoading(view, request)
                                    }
                                )
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

                                CookieManager.getInstance().apply {
                                    acceptCookie()
                                    setAcceptThirdPartyCookies(popUpWebView, true)
                                }

                                val dialog = Dialog(context).apply {
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
                                            if (loginHoYoLABState.securityPopUpUrls.any {
                                                    url?.contains(
                                                        it
                                                    ) == true
                                                }
                                            ) {
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

            is Lce.Error -> {

            }

            Lce.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (loginHoYoLABState.showSslErrorDialog != null) {
        AlertDialog(
            onDismissRequest = {
                loginHoYoLABState.onShowSslErrorDialogChange(null)
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
            confirmButton = {
                TextButton(
                    onClick = loginHoYoLABState::onConfirmSslErrorDialog
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = loginHoYoLABState::onCancelSslErrorDialog
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
                        loginHoYoLABState.showSslErrorDialog?.second?.url?.let {
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