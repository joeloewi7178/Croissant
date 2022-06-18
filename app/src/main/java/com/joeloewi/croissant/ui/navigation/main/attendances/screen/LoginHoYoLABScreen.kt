package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Message
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.*
import com.joeloewi.croissant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val COOKIE = "cookie"

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun LoginHoYoLABScreen(
    navController: NavController
) {
    LoginHoYoLABContent(
        onClickClose = {
            navController.navigateUp()
        },
        onCatchCookie = { cookie ->
            navController.apply {
                previousBackStackEntry?.savedStateHandle?.set(COOKIE, cookie)
                navigateUp()
            }
        }
    )
}

@ExperimentalComposeUiApi
@SuppressLint("SetJavaScriptEnabled")
@ExperimentalMaterial3Api
@Composable
fun LoginHoYoLABContent(
    onClickClose: () -> Unit,
    onCatchCookie: (String) -> Unit
) {
    val hoyolabUrl = "https://m.hoyolab.com/#/home"
    val webViewState = rememberWebViewState(url = hoyolabUrl)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val (currentUrl, onCurrentUrlChange) = remember { mutableStateOf(hoyolabUrl) }
    val webViewNavigator = rememberWebViewNavigator()
    val (showSslErrorDialog, onShowSslErrorDialogChange) = remember {
        mutableStateOf<Pair<SslErrorHandler?, SslError?>?>(
            null
        )
    }

    fun String?.checkContainsHoYoLABCookies(): Boolean =
        if (isNullOrEmpty()) {
            false
        } else {
            listOf("ltoken", "cookie_token").map { contains(it) }.all { it }
        }

    Scaffold(
        topBar = {
            Column {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onClickClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = Icons.Default.Close.name
                            )
                        }
                    },
                    actions = {
                        val incorrectSession = stringResource(id = R.string.incorrect_session)

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
                                val currentCookie =
                                    CookieManager.getInstance().getCookie(hoyolabUrl)

                                if (currentCookie.checkContainsHoYoLABCookies()) {
                                    onCatchCookie(currentCookie)
                                } else {
                                    coroutineScope.launch {
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
                                text = currentUrl,
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
        }
    ) { innerPadding ->
        var navigateUpJob: Job? = remember { null }
        val localContext = LocalContext.current
        val excludedUrls =
            listOf("www.webstatic-sea.mihoyo.com", "www.webstatic-sea.hoyolab.com")
        val securityPopUpUrl =
            "https://m.hoyolab.com/account-system-sea/security.html?origin=hoyolab"
        val darkTheme = isSystemInDarkTheme()

        WebView(
            modifier = Modifier.padding(innerPadding),
            state = webViewState,
            navigator = webViewNavigator,
            onCreated = { webView ->
                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    setSupportMultipleWindows(true)
                    javaScriptCanOpenWindowsAutomatically = true
                    userAgentString = userAgentString.replace("; wv", "")
                }

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && darkTheme) {
                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }

                WebStorage.getInstance().deleteAllData()

                with(webView) {
                    clearCache(true)
                    clearFormData()
                    clearHistory()
                    clearMatches()
                    clearSslPreferences()
                }

                CookieManager.getInstance().apply {
                    acceptCookie()
                    setAcceptThirdPartyCookies(webView, true)
                    flush()
                    removeAllCookies {}
                }
            },
            client = remember {
                object : AccompanistWebViewClient() {
                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        super.onReceivedSslError(view, handler, error)
                        onShowSslErrorDialogChange(handler to error)
                    }

                    override fun shouldInterceptRequest(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): WebResourceResponse? {
                        val currentCookie =
                            CookieManager.getInstance().getCookie(hoyolabUrl)

                        //in this block, codes are executed in io thread.
                        //onCatchCookie callback's role is to execute navController.navigateUp()
                        //which is must executed in main thread.
                        //in addition, shouldInterceptRequest() callback is called many times
                        //but navController.navigateUp() has to be called only once

                        //so, after switching context to main thread, store that job in variable
                        //if the variable is null to ensure execute only once

                        if (currentCookie.checkContainsHoYoLABCookies() && navigateUpJob == null) {
                            navigateUpJob = coroutineScope.launch(Dispatchers.Main) {
                                onCatchCookie(currentCookie)
                            }
                        }

                        return super.shouldInterceptRequest(view, request)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.url?.toString()?.let(onCurrentUrlChange)
                        return if (
                            mutableListOf(hoyolabUrl)
                                .apply { addAll(excludedUrls) }
                                .map { request?.url?.toString()?.contains(it) }
                                .all { it == false }
                        ) {
                            coroutineScope.launch {
                                request?.url?.let {
                                    localContext.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            it
                                        )
                                    )
                                }
                            }
                            true
                        } else {
                            false
                        }
                    }
                }
            },
            chromeClient = remember {
                object : AccompanistWebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        val popUpWebView = WebView(localContext).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                databaseEnabled = true
                                setSupportMultipleWindows(true)
                                javaScriptCanOpenWindowsAutomatically = true
                                userAgentString = userAgentString.replace("; wv", "")
                            }

                            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && darkTheme) {
                                WebSettingsCompat.setForceDark(
                                    settings,
                                    WebSettingsCompat.FORCE_DARK_ON
                                )
                            }
                        }

                        CookieManager.getInstance().apply {
                            acceptCookie()
                            setAcceptThirdPartyCookies(popUpWebView, true)
                        }

                        val dialog = Dialog(localContext).apply {
                            setContentView(popUpWebView)
                        }

                        dialog.window?.run {
                            attributes = attributes?.apply {
                                width = ViewGroup.LayoutParams.MATCH_PARENT
                                height = ViewGroup.LayoutParams.MATCH_PARENT
                            } as WindowManager.LayoutParams
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
                                    if (url == securityPopUpUrl) {
                                        dialog.dismiss()
                                    } else {
                                        dialog.show()
                                    }
                                    super.onPageStarted(view, url, favicon)
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean = false
                            }
                        }

                        resultMsg?.run {
                            (this.obj as WebView.WebViewTransport).webView = popUpWebView
                            sendToTarget()
                        }

                        return true
                    }
                }
            }
        )

        if (showSslErrorDialog != null) {
            AlertDialog(
                onDismissRequest = {
                    onShowSslErrorDialogChange(null)
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                ),
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSslErrorDialog.first?.proceed()
                            onShowSslErrorDialogChange(null)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSslErrorDialog.first?.cancel()
                            onShowSslErrorDialogChange(null)
                        }
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
                            showSslErrorDialog.second?.url?.let {
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