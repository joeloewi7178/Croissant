package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.viewmodel.LoadingViewModel

@ExperimentalMaterial3Api
@Composable
fun LoadingScreen(
    navController: NavController,
    loadingViewModel: LoadingViewModel
) {
    val isAppWidgetConfigured by loadingViewModel.isAppWidgetInitialized.collectAsState()
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
                            popUpTo(ResinStatusWidgetConfigurationDestination.LoadingScreen.route) {
                                inclusive = true
                            }
                        }
                    } else {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen()
                                .generateRoute(appWidgetId)
                        ) {
                            popUpTo(ResinStatusWidgetConfigurationDestination.LoadingScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                }.onFailure { cause ->
                    FirebaseCrashlytics.getInstance().apply {
                        log(ResinStatusWidgetConfigurationDestination.LoadingScreen.route)
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

@ExperimentalMaterial3Api
@Composable
private fun LoadingContent() {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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
}