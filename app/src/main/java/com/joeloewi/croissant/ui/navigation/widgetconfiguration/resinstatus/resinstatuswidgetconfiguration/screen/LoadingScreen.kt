package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    loadingViewModel: LoadingViewModel,
    appWidgetId: Int
) {
    val isAppWidgetConfigured by loadingViewModel.isAppWidgetInitialized.collectAsState()

    LaunchedEffect(loadingViewModel) {
        loadingViewModel.findResinStatusWidgetByAppWidgetId(appWidgetId)
    }

    LaunchedEffect(isAppWidgetConfigured) {
        when (isAppWidgetConfigured) {
            is Lce.Content -> {
                runCatching {
                    if (isAppWidgetConfigured.content == true) {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen()
                                .generateRoute(appWidgetId)
                        )
                    } else {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen()
                                .generateRoute(appWidgetId)
                        )
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