package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.ILCE
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginHoYoLABScreen(
    loginHoYoLABViewModel: LoginHoYoLABViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onNavigateUpWithResult: (cookie: String) -> Unit
) {
    val removeAllCookiesState by loginHoYoLABViewModel.removeAllCookies.collectAsStateWithLifecycle()
    val automaticallyCheckedCookie by loginHoYoLABViewModel.automaticallyCheckedCookie.collectAsStateWithLifecycle()
    val manuallyCheckedCookie by loginHoYoLABViewModel.manuallyCheckedCookie.collectAsStateWithLifecycle()

    LoginHoYoLABContent(
        hoyolabUrl = loginHoYoLABViewModel.hoyolabUrl,
        removeAllCookiesState = { removeAllCookiesState },
        automaticallyCheckedCookie = { automaticallyCheckedCookie },
        manuallyCheckedCookie = { manuallyCheckedCookie },
        onNavigateUp = onNavigateUp,
        onNavigateUpWithResult = onNavigateUpWithResult,
        onCheckCookie = loginHoYoLABViewModel::onCheckCookie
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginHoYoLABContent(
    hoyolabUrl: String,
    removeAllCookiesState: () -> LCE<Boolean>,
    automaticallyCheckedCookie: () -> String,
    manuallyCheckedCookie: () -> ILCE<Pair<Boolean, String>>,
    onNavigateUp: () -> Unit,
    onNavigateUpWithResult: (cookie: String) -> Unit,
    onCheckCookie: (Boolean) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val incorrectSession = stringResource(id = R.string.incorrect_session)
    val context = LocalContext.current
    val activity = LocalActivity.current
    val webViewState = rememberWebViewState(url = hoyolabUrl)
    val webViewNavigator = rememberWebViewNavigator()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
    val cookieManager = remember { CookieManager.getInstance() }

    LaunchedEffect(Unit) {
        snapshotFlow(automaticallyCheckedCookie).catch { }
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .filter { it.isNotEmpty() }
            .collect {
                withContext(Dispatchers.Main.immediate) {
                    onNavigateUpWithResult(it)
                }
            }
    }

    LaunchedEffect(Unit) {
        snapshotFlow(manuallyCheckedCookie).catch { }
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect {
                when (it) {
                    is ILCE.Content -> {
                        if (it.content.first) {
                            withContext(Dispatchers.Main.immediate) {
                                onNavigateUpWithResult(it.content.second)
                            }
                        } else {
                            coroutineScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
                                with(snackbarHostState) {
                                    currentSnackbarData?.dismiss()
                                    showSnackbar(message = incorrectSession)
                                }
                            }
                        }
                    }

                    else -> {

                    }
                }
            }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            with(lifecycleOwner) {
                                lifecycleScope.launch(CoroutineExceptionHandler { _, _ -> }) {
                                    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                        onNavigateUp()
                                    }
                                }
                            }
                        }) {
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        when (removeAllCookiesState()) {
            is LCE.Content -> {
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

                        cookieManager.apply {
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
                                onCheckCookie(false)

                                return super.shouldInterceptRequest(
                                    view,
                                    request
                                )
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean = if ((mutableListOf(hoyolabUrl) + excludedUrls).all {
                                    (request?.url?.toString()?.contains(it) == false)
                                }) {
                                coroutineScope.launch {
                                    request?.url?.let {
                                        val intent = Intent(Intent.ACTION_VIEW, it)

                                        if (intent.resolveActivity(activity.packageManager) != null) {
                                            activity.startActivity(intent)
                                        }
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

                                cookieManager.apply {
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

            is LCE.Error -> {

            }

            LCE.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

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