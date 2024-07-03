package com.joeloewi.croissant.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.CancellationException

//under LocalActivity
@Composable
fun RequireAppUpdate(
    appUpdateResultState: AppUpdateResult,
    content: @Composable () -> Unit
) {
    val activity = LocalActivity.current
    val requestCode = remember { 22050 }
    val updatedContent by rememberUpdatedState(newValue = content)

    LaunchedEffect(appUpdateResultState) {
        when (appUpdateResultState) {
            is AppUpdateResult.Available -> {
                appUpdateResultState.runCatching {
                    startImmediateUpdate(activity = activity, requestCode = requestCode)
                }.onFailure { cause ->
                    if (cause is CancellationException) {
                        throw cause
                    }
                }
            }

            is AppUpdateResult.Downloaded -> {
                appUpdateResultState.runCatching {
                    completeUpdate()
                }.onFailure { cause ->
                    if (cause is CancellationException) {
                        throw cause
                    }
                }
            }

            else -> {

            }
        }
    }

    updatedContent()
}