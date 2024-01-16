package com.joeloewi.croissant.util

import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.getSystemService
import androidx.work.CoroutineWorker

suspend fun <T> CoroutineWorker.withBoundNetwork(block: suspend () -> T): T {
    val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (network != null) {
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