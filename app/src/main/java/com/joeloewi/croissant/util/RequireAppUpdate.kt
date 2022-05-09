package com.joeloewi.croissant.util

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.ktx.totalBytesToDownload
import com.joeloewi.croissant.ui.theme.DefaultDp
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
    val (appUpdateState, onAppUpdateStateChange) = remember {
        mutableStateOf<AppUpdateResult>(
            AppUpdateResult.NotAvailable
        )
    }

    LaunchedEffect(LocalLifecycleOwner.current) {
        //can't use catch block and collectAsState() at once
        AppUpdateManagerFactory.create(context).requestUpdateFlow()
            .catch { it.printStackTrace() }
            .onEach {
                onAppUpdateStateChange(it)
            }.launchIn(this)
    }

    with(appUpdateState) {
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