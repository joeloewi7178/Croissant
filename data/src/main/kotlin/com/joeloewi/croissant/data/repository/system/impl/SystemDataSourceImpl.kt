package com.joeloewi.croissant.data.repository.system.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.text.format.DateFormat
import android.webkit.CookieManager
import androidx.core.content.PackageManagerCompat
import androidx.core.content.UnusedAppRestrictionsConstants
import com.joeloewi.croissant.data.di.ApplicationHandlerDispatcher
import com.joeloewi.croissant.data.repository.system.SystemDataSource
import com.joeloewi.croissant.data.system.RootChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject
import kotlin.coroutines.resume

class SystemDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationHandlerDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val applicationHandler: Handler,
    private val rootChecker: RootChecker,
    private val connectivityManager: ConnectivityManager
) : SystemDataSource {

    override fun is24HourFormat(): Flow<Boolean> = callbackFlow {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(DateFormat.is24HourFormat(context))
            }
        }

        val intentFilter = IntentFilter(Intent.ACTION_TIME_CHANGED)

        context.registerReceiver(broadcastReceiver, intentFilter, null, applicationHandler)

        awaitClose { context.unregisterReceiver(broadcastReceiver) }
    }.flowOn(Dispatchers.IO)

    override suspend fun isDeviceRooted(): Boolean = rootChecker.isDeviceRooted()

    override suspend fun isUnusedAppRestrictionEnabled(): Result<Boolean> =
        withContext(Dispatchers.IO) {
            runCatching {
                PackageManagerCompat.getUnusedAppRestrictionsStatus(context).await()
            }.mapCatching {
                when (it) {
                    UnusedAppRestrictionsConstants.API_30_BACKPORT, UnusedAppRestrictionsConstants.API_30, UnusedAppRestrictionsConstants.API_31 -> {
                        true
                    }

                    UnusedAppRestrictionsConstants.DISABLED -> {
                        false
                    }

                    else -> {
                        throw IllegalStateException()
                    }
                }
            }
        }

    override suspend fun removeAllCookies(): Result<Boolean> = runCatching {
        withContext(Dispatchers.IO) {
            withContext(coroutineDispatcher) {
                //CookieManager.removeAllCookies() should be called on thread that has looper
                suspendCancellableCoroutine { cont ->
                    CookieManager.getInstance().removeAllCookies {
                        cont.resume(it)
                    }
                    cont.invokeOnCancellation { }
                }
            }
        }
    }

    override suspend fun canPerformDnsLookup(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val networkConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return@withContext false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                    ?: return@withContext false

                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } else {
                connectivityManager.activeNetworkInfo?.isConnected == true
            }

            val isDnsLookupSuccessful = runCatching {
                !InetAddress.getByName("hoyolab.com").hostAddress.isNullOrEmpty()
            }.getOrDefault(false)

            networkConnected && isDnsLookupSuccessful
        }.getOrDefault(false)
    }

    override suspend fun isNetworkVpn(): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            with(connectivityManager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getNetworkCapabilities(activeNetwork)?.hasTransport(
                        NetworkCapabilities.TRANSPORT_VPN
                    )
                } else {
                    activeNetworkInfo?.type == ConnectivityManager.TYPE_VPN
                } ?: false
            }
        }.getOrDefault(false)
    }
}