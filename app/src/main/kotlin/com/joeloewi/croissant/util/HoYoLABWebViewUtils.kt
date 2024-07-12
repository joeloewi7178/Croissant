package com.joeloewi.croissant.util

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
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Immutable
import androidx.core.view.WindowCompat
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.joeloewi.croissant.BuildConfig
import kotlinx.collections.immutable.ImmutableList

/*
 *    Copyright 2024. joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

@SuppressLint("SetJavaScriptEnabled")
val onHoYoLABWebViewCreated: (webView: WebView) -> Unit = { webview ->
    with(webview) {
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

    CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true)
}

@Suppress("DEPRECATION")
@Immutable
class HoYoLABWebViewClient(
    private val allowedUrls: ImmutableList<String>,
    private val onShowSSLErrorDialog: (handler: SslErrorHandler, error: SslError) -> Unit,
    private val onCheckCookie: (isManuallyCheck: Boolean) -> Unit,
    private val onLaunchUri: (uri: Uri) -> Unit
) : AccompanistWebViewClient() {
    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler?,
        error: SslError?
    ) {
        super.onReceivedSslError(view, handler, error)

        if (handler != null && error != null) {
            onShowSSLErrorDialog(handler, error)
        }
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
    ): Boolean {
        val uri = request?.url

        if (allowedUrls.any { uri?.toString()?.contains(it) == true }) {
            uri?.let(onLaunchUri)
            return true
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}

@Suppress("DEPRECATION")
@Immutable
class HoYoLABWebChromeClient(
    private val securityPopUpUrls: ImmutableList<String>
) : AccompanistWebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean =
        if (BuildConfig.DEBUG) {
            super.onConsoleMessage(consoleMessage)
        } else {
            true
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        val context = view?.context ?: return false

        val popUpWebView = WebView(context).apply {
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

        CookieManager.getInstance().setAcceptThirdPartyCookies(popUpWebView, true)

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

        dialog.setOnDismissListener {
            popUpWebView.destroy()
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