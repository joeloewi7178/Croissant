package com.joeloewi.croissant.util

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.ktx.totalBytesToDownload
import com.joeloewi.croissant.BuildConfig
import com.joeloewi.croissant.ui.theme.DefaultDp

//under LocalActivity
@ExperimentalMaterial3Api
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
    val appUpdate by remember {
        if (BuildConfig.DEBUG) {
            FakeAppUpdateManager(context)
        } else {
            AppUpdateManagerFactory.create(context)
        }
    }.requestUpdateFlow().collectAsState(initial = AppUpdateResult.NotAvailable)

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

@ExperimentalMaterial3Api
@Composable
private fun InProgressScreen(
    progress: Float
) {
    Scaffold(
        topBar = {
            Spacer(
                modifier = Modifier.padding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .asPaddingValues()
                )
            )
        },
        bottomBar = {
            Spacer(
                modifier = Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .fillMaxWidth(),
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
}