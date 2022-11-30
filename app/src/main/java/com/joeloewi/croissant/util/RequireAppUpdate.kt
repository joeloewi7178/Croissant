package com.joeloewi.croissant.util

import androidx.compose.runtime.*
import com.google.android.play.core.ktx.AppUpdateResult

//under LocalActivity
@Composable
fun RequireAppUpdate(
    appUpdateResultState: AppUpdateResult,
    content: @Composable () -> Unit
) {
    val requestCode = remember { 22050 }
    val activity = LocalActivity.current
    val updatedContent by rememberUpdatedState(newValue = content)

    LaunchedEffect(appUpdateResultState) {
        when (appUpdateResultState) {
            is AppUpdateResult.Available -> {
                appUpdateResultState.runCatching {
                    startImmediateUpdate(activity = activity, requestCode = requestCode)
                }.onSuccess {

                }.onFailure {

                }
            }
            is AppUpdateResult.Downloaded -> {
                appUpdateResultState.runCatching {
                    completeUpdate()
                }.onSuccess {

                }.onFailure {

                }
            }
            else -> {

            }
        }
    }

    updatedContent()
}