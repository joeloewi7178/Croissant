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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.*
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

const val COOKIE = "cookie"

@Composable
fun LoginHoYoLABScreen(
    loginHoYoLABViewModel: LoginHoYoLABViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateUpWithResult: (cookie: String) -> Unit
) {
    val removeAllCookiesState by loginHoYoLABViewModel.removeAllCookies.collectAsStateWithLifecycle()
    val currentCookie by loginHoYoLABViewModel.currentCookie.collectAsStateWithLifecycle()

    LoginHoYoLABContent(
        removeAllCookiesState = { removeAllCookiesState },
        currentCookie = { currentCookie },
        onNavigateUp = onNavigateUp,
        onNavigateUpWithResult = onNavigateUpWithResult,
        onCurrentCookieChange = loginHoYoLABViewModel::setCurrentCookie
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginHoYoLABContent(
    removeAllCookiesState: () -> Lce<Boolean>,
    currentCookie: () -> String,
    onNavigateUp: () -> Unit,
    onNavigateUpWithResult: (cookie: String) -> Unit,
    onCurrentCookieChange: (String) -> Unit
) {
    val incorrectSession = stringResource(id = R.string.incorrect_session)
    val context = LocalContext.current
    val activity = LocalActivity.current
    val hoyolabUrl = remember { "https://m.hoyolab.com" }
    val webViewState = rememberWebViewState(url = hoyolabUrl)
    val webViewNavigator = rememberWebViewNavigator()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val cookieKeys = remember { listOf("ltoken_v2", "ltmid_v2").toImmutableList() }
    val securityPopUpUrls = remember {
        listOf(
            "https://account.hoyolab.com/security.html",
            "https://m.hoyolab.com/account-system-sea/security.html",
            "about:blank",
            "https://account.hoyolab.com/single-page/cross-login.html"
        )
    }.toImmutableList()
    val excludedUrls = remember {
        listOf("www.webstatic-sea.mihoyo.com", "www.webstatic-sea.hoyolab.com")
    }.toImmutableList()
    var showSslErrorDialog by remember { mutableStateOf<Pair<SslErrorHandler?, SslError?>?>(null) }

    LaunchedEffect(Unit) {
        snapshotFlow(currentCookie).catch { }.flowOn(Dispatchers.IO).filter { it.isNotEmpty() }
            .collect {
                onNavigateUpWithResult(it)
            }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
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
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    val hoyolabCookie =
                                        CookieManager.getInstance().getCookie(hoyolabUrl)

                                    if (cookieKeys.map { hoyolabCookie.contains(it) }.all { it }) {
                                        withContext(Dispatchers.Main) {
                                            onNavigateUpWithResult(hoyolabCookie)
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar(
                                            message = incorrectSession
                                        )
                                    }
                                }
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
                                text = webViewState.pageTitle ?: "Title",
                                style = MaterialTheme.typography.titleMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = webViewState.lastLoadedUrl ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                )
                with(webViewState.loadingState) {
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
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        when (removeAllCookiesState()) {
            is Lce.Content -> {
                WebView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = webViewState,
                    navigator = webViewNavigator,
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
                                showSslErrorDialog = handler to error
                            }

                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                //in this block, codes are executed in io thread.
                                //and called multiple times.
                                //but we only need first value.
                                //use stateflow in view model to distinct and get first value

                                val cookie =
                                    CookieManager.getInstance().getCookie(hoyolabUrl)

                                if (cookieKeys.map { cookie?.contains(it) == true }.all { it }) {
                                    onCurrentCookieChange(cookie)
                                }

                                return runBlocking(Dispatchers.IO) {
                                    super.shouldInterceptRequest(
                                        view,
                                        request
                                    )
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean = if ((mutableListOf(hoyolabUrl) + excludedUrls).all {
                                    (request?.url?.toString()?.contains(it) == false)
                                }) {
                                coroutineScope.launch {
                                    request?.url?.let {
                                        activity.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                it
                                            )
                                        )
                                    }
                                }
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
                                            if (securityPopUpUrls.any { url?.contains(it) == true }) {
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

    if (showSslErrorDialog != null) {
        AlertDialog(
            onDismissRequest = {
                showSslErrorDialog = null
            },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
            confirmButton = {
                TextButton(
                    onClick = { showSslErrorDialog?.first?.proceed() }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSslErrorDialog?.first?.cancel() }
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
                        showSslErrorDialog?.second?.url?.let {
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