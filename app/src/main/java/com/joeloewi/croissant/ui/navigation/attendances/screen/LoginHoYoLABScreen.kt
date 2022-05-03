package com.joeloewi.croissant.ui.navigation.attendances.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.webkit.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
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
                    navigationIcon = {
                        IconButton(onClick = onClickClose) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = Icons.Default.Close.name
                            )
                        }
                    },
                    actions = {
                        ReloadOrStopLoading(
                            isLoading = webViewState.isLoading,
                            webView = webView,
                            reload = {
                                IconButton(
                                    onClick = {
                                        it?.reload()
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
                                        it?.stopLoading()
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
                                            message = "접속정보가 정확하지 않습니다. 다시 로그인해주세요."
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
        val excludedUrls = listOf("www.webstatic-sea.mihoyo.com", "www.webstatic-sea.hoyolab.com")
        val darkTheme = isSystemInDarkTheme()

        WebView(
            state = webViewState,
            onCreated = { webView ->
                onWebViewChange(webView)

                webView.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }

                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && darkTheme) {
                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        FORCE_DARK_ON
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
                    flush()
                    removeAllCookies {}
                }
            },
            client = object : AccompanistWebViewClient() {
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
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