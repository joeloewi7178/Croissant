package com.joeloewi.croissant.ui.navigation.attendances.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun LoginHoYoLABScreen(
    navController: NavController,
    loginHoYoLABViewModel: LoginHoYoLABViewModel
) {
    LoginHoYoLABContent(
        onClickClose = {
            navController.navigateUp()
        },
        onCatchCookie = { cookie ->
            navController.apply {
                previousBackStackEntry?.savedStateHandle?.set("cookie", cookie)
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
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val (currentUrl, onCurrentUrlChange) = remember { mutableStateOf(hoyolabUrl) }
    val (isLoading, onIsLoadingChange) = remember { mutableStateOf(false) }
    val (webPageTitle, onWebPageTitleChange) = remember { mutableStateOf("Title") }
    val (webPageProgress, onWebPageProgressChange) = remember { mutableStateOf(0) }
    val (webView, onWebViewChange) = remember { mutableStateOf<WebView?>(null) }
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
                    modifier = Modifier.padding(
                        WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                            .asPaddingValues()
                    ),
                    navigationIcon = {
                        IconButton(onClick = onClickClose) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = Icons.Outlined.Close.name
                            )
                        }
                    },
                    actions = {
                        ReloadOrStopLoading(
                            isLoading = isLoading,
                            webView = webView,
                            reload = {
                                IconButton(
                                    onClick = {
                                        it?.reload()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Refresh,
                                        contentDescription = Icons.Outlined.Refresh.name
                                    )
                                }
                            },
                            stopLoading = {
                                IconButton(
                                    onClick = {
                                        it?.stopLoading()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = Icons.Outlined.Close.name
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
                                            message = "접속정보가 정확하지 않습니다. 다시 로그인해주세요."
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Done,
                                contentDescription = Icons.Outlined.Done.name
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = webPageTitle,
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
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LinearProgressIndicator(progress = webPageProgress.toFloat())
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        val (canGoBack, onCanGoBackChange) = remember { mutableStateOf(false) }
        var navigateUpJob: Job? = remember { null }
        val localContext = LocalContext.current
        val mihoyoUrl = "www.webstatic-sea.mihoyo.com"
        val (webViewState, onWebViewStateChange) = rememberSaveable { mutableStateOf<Bundle?>(null) }
        val darkTheme = isSystemInDarkTheme()

        BackHandler(canGoBack) {
            webView?.goBack()
        }

        LaunchedEffect(darkTheme, webView) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && webView != null && darkTheme) {
                WebSettingsCompat.setForceDark(webView.settings, FORCE_DARK_ON)
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { androidViewContext ->
                WebView(androidViewContext).apply {

                    CookieManager.getInstance().acceptThirdPartyCookies(this)

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                            onIsLoadingChange(true)
                        }

                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            super.onReceivedSslError(view, handler, error)
                            onShowSslErrorDialogChange(handler to error)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            onIsLoadingChange(false)
                        }

                        override fun doUpdateVisitedHistory(
                            view: WebView?,
                            url: String?,
                            isReload: Boolean
                        ) {
                            super.doUpdateVisitedHistory(view, url, isReload)

                            if (url != null && currentUrl != url) {
                                onCurrentUrlChange(url)
                            }
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val currentCookie =
                                CookieManager.getInstance().getCookie(hoyolabUrl)

                            if (currentCookie.checkContainsHoYoLABCookies() && navigateUpJob == null) {
                                navigateUpJob = coroutineScope.launch {
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
                                listOf(hoyolabUrl, mihoyoUrl).map {
                                    request?.url?.toString()?.contains(it)
                                }.all { it == false }
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

                    webChromeClient = object : WebChromeClient() {
                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            title?.let(onWebPageTitleChange)
                            super.onReceivedTitle(view, title)
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            onWebPageProgressChange(newProgress)
                            super.onProgressChanged(view, newProgress)
                        }
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    }

                    webViewState?.let { restoreState(it) }
                }.also(onWebViewChange)
            }
        ) { view ->
            onCanGoBackChange(view.canGoBack())

            if (webViewState == null) {
                CookieManager.getInstance().apply {
                    flush()
                    removeAllCookies {
                        view.loadUrl(currentUrl)
                    }
                }
            }

            onWebViewStateChange(
                bundleOf().apply {
                    view.saveState(this)
                }
            )
        }

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
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSslErrorDialog.first?.cancel()
                            onShowSslErrorDialogChange(null)
                        }
                    ) {
                        Text(text = "취소")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = Icons.Outlined.Warning.name
                    )
                },
                title = {
                    Text(text = "인증서 오류")
                },
                text = {
                    Text(
                        text = buildAnnotatedString {
                            append("방문하려는 웹사이트")
                            showSslErrorDialog.second?.url?.let {
                                append("(${it})")
                            }
                            append("의 인증서에 오류가 있습니다. 계속 진행하시겠습니까?")
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun ReloadOrStopLoading(
    isLoading: Boolean,
    webView: WebView?,
    reload: @Composable (WebView?) -> Unit,
    stopLoading: @Composable (WebView?) -> Unit
) {
    if (!isLoading) {
        reload(webView)
    } else {
        stopLoading(webView)
    }
}