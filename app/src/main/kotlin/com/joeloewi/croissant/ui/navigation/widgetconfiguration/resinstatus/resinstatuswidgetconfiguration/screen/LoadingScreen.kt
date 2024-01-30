package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.viewmodel.LoadingViewModel

@Composable
fun LoadingScreen(
    navController: NavHostController,
    loadingViewModel: LoadingViewModel
) {
    val isAppWidgetConfigured by loadingViewModel.isAppWidgetInitialized.collectAsStateWithLifecycle()
    val appWidgetId = remember { loadingViewModel.appWidgetId }

    LaunchedEffect(isAppWidgetConfigured) {
        when (isAppWidgetConfigured) {
            is LCE.Content -> {
                runCatching {
                    if (isAppWidgetConfigured.content == true) {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen()
                                .generateRoute(appWidgetId)
                        ) {
                            navController.currentDestination?.let {
                                popUpTo(it.id) {
                                    inclusive = true
                                }
                            }
                        }
                    } else {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen()
                                .generateRoute(appWidgetId)
                        ) {
                            navController.currentDestination?.let {
                                popUpTo(it.id) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }.onFailure { cause ->
                    Firebase.crashlytics.apply {
                        log(ResinStatusWidgetConfigurationDestination.LoadingScreen().route)
                        recordException(cause)
                    }
                }
            }

            else -> {

            }
        }
    }

    LoadingContent()
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

        }
    }
}