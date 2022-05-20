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
import androidx.compose.ui.res.stringResource
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.requestUpdateFlow
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.R
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
            Text(text = stringResource(id = R.string.latest_version_of_app_is_downloading))
            Text(text = stringResource(id = R.string.install_after_download_automatically))
        }
    }
}