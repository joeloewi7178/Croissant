package com.joeloewi.croissant.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

suspend fun <T> CoroutineWorker.withBoundNetwork(block: suspend () -> T): T {
    val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (network != null) {
            runCatching {
                connectivityManager?.getNetworkCapabilities(network)?.apply {
                    Firebase.crashlytics.log("isVpn=${hasTransport(NetworkCapabilities.TRANSPORT_VPN)}")
                }
            }

            val shouldBeUnbound = connectivityManager?.bindProcessToNetwork(network)

            try {
                block()
            } finally {
                if (shouldBeUnbound == true) {
                    connectivityManager.bindProcessToNetwork(null)
                }
            }
        } else {
            block()
        }
    } else {
        block()
    }
}