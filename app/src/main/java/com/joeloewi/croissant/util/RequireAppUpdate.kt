package com.joeloewi.croissant.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

//under LocalActivity
@ExperimentalMaterial3Api
@Composable
fun RequireAppUpdate(
    content: @Composable () -> Unit
) {
    val requestCode = 22050
    val context = LocalContext.current
    val activity = LocalActivity.current
    val (appUpdateState, onAppUpdateStateChange) = remember {
        mutableStateOf<AppUpdateResult>(
            AppUpdateResult.NotAvailable
        )
    }

    LaunchedEffect(LocalLifecycleOwner.current) {
        //can't use catch block and collectAsState() at once
        runCatching {
            AppUpdateManagerFactory.create(context)
        }.mapCatching { appUpdateManager ->
            appUpdateManager.requestUpdateFlow()
                .catch { it.printStackTrace() }
                .onEach {
                    onAppUpdateStateChange(it)
                }.launchIn(this)
        }.onFailure { cause ->
            FirebaseCrashlytics.getInstance().apply {
                log("RequireAppUpdate")
                recordException(cause)
            }
        }
    }

    with(appUpdateState) {
        when (this) {
            is AppUpdateResult.Available -> {
                kotlin.runCatching {
                    startImmediateUpdate(activity = activity, requestCode = requestCode)
                }.onSuccess {

                }.onFailure { cause ->
                    cause.printStackTrace()
                }
            }
            is AppUpdateResult.Downloaded -> {
                LaunchedEffect(this) {
                    kotlin.runCatching {
                        completeUpdate()
                    }.onSuccess {

                    }.onFailure { cause ->
                        cause.printStackTrace()
                    }
                }
            }
            else -> {

            }
        }
    }

    content()
}