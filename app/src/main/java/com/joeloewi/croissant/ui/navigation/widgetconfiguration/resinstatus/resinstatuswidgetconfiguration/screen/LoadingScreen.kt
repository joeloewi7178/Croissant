package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.state.Lce
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
            is Lce.Content -> {
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
                    FirebaseCrashlytics.getInstance().apply {
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
            CircularProgressIndicator()
        }
    }
}