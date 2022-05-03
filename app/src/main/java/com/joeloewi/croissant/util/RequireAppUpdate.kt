package com.joeloewi.croissant.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.ktx.totalBytesToDownload
import com.joeloewi.croissant.ui.theme.DefaultDp

//under LocalActivity
@Composable
fun RequireAppUpdate(
    inProgressContent: @Composable ((progress: Float) -> Unit) = { progress ->
        InProgressScreen(progress = progress)
    },
    content: @Composable () -> Unit
) {
    val requestCode = 22050
    val context = LocalContext.current
    val activity = LocalActivity.current
    val appUpdate by FakeAppUpdateManager(context).requestUpdateFlow()
        .collectAsState(initial = AppUpdateResult.NotAvailable)

    with(appUpdate) {
        when (this) {
            AppUpdateResult.NotAvailable -> {
                content()
            }
            is AppUpdateResult.Available -> {
                kotlin.runCatching {
                    startImmediateUpdate(activity = activity, requestCode = requestCode)
                }.onSuccess {

                }.onFailure { cause ->
                    cause.printStackTrace()
                }
            }
            is AppUpdateResult.InProgress -> {
                val progress = with(installState) {
                    bytesDownloaded.toFloat() / totalBytesToDownload.toFloat()
                }

                inProgressContent(progress = progress)
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
        }
    }
}

@Composable
private fun InProgressScreen(
    progress: Float
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = DefaultDp,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(progress = progress)
        Text(text = "새로운 버전의 앱 다운로드 중")
        Text(text = "다운로드 완료 후 자동으로 설치를 진행합니다.")
    }
}