package com.joeloewi.croissant.ui.navigation.attendances.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun LoginHoYoLABScreen(navController: NavController) {
    LoginHoYoLABContent(
        onClickClose = {
            navController.popBackStack()
        },
        onCatchCookie = { cookie ->
            navController.apply {
                previousBackStackEntry?.savedStateHandle?.set("cookie", cookie)
                popBackStack()
            }
        }
    )
}

@SuppressLint("SetJavaScriptEnabled")
@ExperimentalMaterial3Api
@Composable
fun LoginHoYoLABContent(
    onClickClose: () -> Unit,
    onCatchCookie: (String) -> Unit
) {
    val hoyolabUrl = "https://m.hoyolab.com"
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val (currentUrl, onCurrentUrlChange) = remember { mutableStateOf(hoyolabUrl) }
    val (isLoading, onIsLoadingChange) = remember { mutableStateOf(false) }
    val (webPageTitle, onWebPageTitleChange) = remember { mutableStateOf("Title") }
    val (webPageProgress, onWebPageProgressChange) = remember { mutableStateOf(0) }
    val (webView, onWebViewChange) = remember { mutableStateOf<WebView?>(null) }

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
                        rememberInsetsPaddingValues(
                            LocalWindowInsets.current.statusBars,
                            applyBottom = false,
                        )
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
        var popBackStackJob: Job? = remember { null }
        val localContext = LocalContext.current
        val mihoyoUrl = "www.webstatic-sea.mihoyo.com"

        LaunchedEffect(webView) {
            CookieManager.getInstance().apply {
                flush()
                removeAllCookies {
                    webView?.loadUrl(hoyolabUrl)
                }
            }
        }

        BackHandler(canGoBack) {
            webView?.goBack()
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { androidViewContext ->
                WebView(androidViewContext).apply {
                    CookieManager.getInstance().acceptThirdPartyCookies(this)

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            onIsLoadingChange(true)
                            url?.let(onCurrentUrlChange)
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            onIsLoadingChange(false)
                            url?.let(onCurrentUrlChange)
                            super.onPageFinished(view, url)
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val currentCookie =
                                CookieManager.getInstance().getCookie(hoyolabUrl)

                            if (currentCookie.checkContainsHoYoLABCookies() && popBackStackJob == null) {
                                popBackStackJob = coroutineScope.launch {
                                    onCatchCookie(currentCookie)
                                }
                            }

                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean =
                            if (
                                listOf(hoyolabUrl, mihoyoUrl).map {
                                    request?.url?.toString()?.contains(it)
                                }.any { it == true }
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
                                super.shouldOverrideUrlLoading(view, request)
                            }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onReceivedTitle(view: WebView?, title: String?) {
                            title?.let {
                                onWebPageTitleChange(it)
                            }
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
                    }
                }.also(onWebViewChange)
            }
        ) { view ->
            onCanGoBackChange(view.canGoBack())
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