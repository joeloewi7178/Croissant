package com.joeloewi.croissant.ui.navigation.main.attendances.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.util.HoYoLABWebChromeClient
import com.joeloewi.croissant.util.HoYoLABWebViewClient
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.onHoYoLABWebViewCreated
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
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.animateContentSize()
            ) {
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

                when (val loadingState = webViewState.loadingState) {
                    is LoadingState.Loading -> {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = { loadingState.progress }
                        )
                    }

                    LoadingState.Finished -> {

                    }

                    LoadingState.Initializing -> {

                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        when (state.isCookieCleared) {
            is LCE.Content -> {
                WebView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = webViewState,
                    navigator = webViewNavigator,
                    onCreated = onHoYoLABWebViewCreated,
                    onDispose = { it.destroy() },
                    client = remember {
                        HoYoLABWebViewClient(
                            allowedUrls = state.allowedUrls,
                            onShowSSLErrorDialog = { handler, error -> onShowSSLErrorDialog(handler to error) },
                            onCheckCookie = onCheckCookie,
                            onLaunchUri = onLaunchUri
                        )
                    },
                    chromeClient = remember { HoYoLABWebChromeClient(securityPopUpUrls = state.securityPopUpUrls) }
                )
            }

            else -> {
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