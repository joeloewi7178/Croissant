package com.joeloewi.croissant.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch

//under LocalActivity
@Composable
fun RequireAppUpdate(
    appUpdateResultState: () -> AppUpdateResult,
    content: @Composable () -> Unit
) {
    val activity = LocalActivity.current
    val updatedContent by rememberUpdatedState(newValue = content)

    LaunchedEffect(Unit) {
        val requestCode = 22050

        snapshotFlow(appUpdateResultState).catch { }.collect {
            when (it) {
                is AppUpdateResult.Available -> {
                    appUpdateResultState.runCatching {
                        it.startImmediateUpdate(activity = activity, requestCode = requestCode)
                    }.onSuccess {

                    }.onFailure { cause ->
                        if (cause is CancellationException) {
                            throw cause
                        }
                    }
                }

                is AppUpdateResult.Downloaded -> {
                    appUpdateResultState.runCatching {
                        it.completeUpdate()
                    }.onSuccess {

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
    }

    updatedContent()
}