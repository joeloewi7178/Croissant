package com.joeloewi.croissant.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.android.play.core.ktx.AppUpdateResult

//under LocalActivity
@ExperimentalMaterial3Api
@Composable
fun RequireAppUpdate(
    appUpdateResultState: AppUpdateResult,
    content: @Composable () -> Unit
) {
    val requestCode = remember { 22050 }
    val activity = LocalActivity.current

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

    content()
}