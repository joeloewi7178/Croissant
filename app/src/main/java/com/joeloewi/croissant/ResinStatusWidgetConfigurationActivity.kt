package com.joeloewi.croissant

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.ResinStatusWidgetConfigurationNavigation
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.CreateResinStatusWidgetScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.LoadingScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.ResinStatusWidgetDetailScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import com.joeloewi.croissant.viewmodel.LoadingViewModel
import com.joeloewi.croissant.viewmodel.MainViewModel
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

//app widget configuration intent does not provide app widget provider's name
@FlowPreview
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class ResinStatusWidgetConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            CroissantTheme(
                window = window
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalActivity provides this) {
                        val mainViewModel: MainViewModel = hiltViewModel()

                        ResinStatusWidgetConfigurationApp()
                    }
                }
            }
        }
    }
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetConfigurationApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember(navBackStackEntry) { derivedStateOf { navBackStackEntry?.destination } }
    val context = LocalContext.current
    val activity = LocalActivity.current
    val appWidgetId by remember {
        lazy {
            activity.intent?.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        }
    }

    LaunchedEffect(currentDestination) {
        FirebaseAnalytics.getInstance(context).logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to currentDestination?.route,
                FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
            )
        )
    }

    Box(
        modifier = Modifier.navigationBarsPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = ResinStatusWidgetConfigurationNavigation.Configuration.route
        ) {
            navigation(
                startDestination = ResinStatusWidgetConfigurationDestination.LoadingScreen.route,
                route = ResinStatusWidgetConfigurationNavigation.Configuration.route
            ) {
                composable(
                    route = ResinStatusWidgetConfigurationDestination.LoadingScreen.route,
                    arguments = listOf(
                        navArgument(ResinStatusWidgetConfigurationDestination.LoadingScreen.route) {
                            type = NavType.IntType
                            defaultValue = appWidgetId
                        }
                    )
                ) { navBackStackEntry ->
                    val loadingViewModel: LoadingViewModel = hiltViewModel(navBackStackEntry)

                    LoadingScreen(
                        navController = navController,
                        loadingViewModel = loadingViewModel
                    )
                }

                composable(
                    route = ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen().route,
                    arguments = ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen().arguments.map { argument ->
                        navArgument(argument.first) {
                            type = argument.second
                        }
                    },
                ) { navBackStackEntry ->
                    val createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel =
                        hiltViewModel(navBackStackEntry)

                    CreateResinStatusWidgetScreen(
                        navController = navController,
                        createResinStatusWidgetViewModel = createResinStatusWidgetViewModel
                    )
                }

                composable(
                    route = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen().route,
                    arguments = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen().arguments.map { argument ->
                        navArgument(argument.first) {
                            type = argument.second
                        }
                    }
                ) { navBackStackEntry ->
                    val resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel =
                        hiltViewModel(navBackStackEntry)

                    ResinStatusWidgetDetailScreen(
                        navController = navController,
                        resinStatusWidgetDetailViewModel = resinStatusWidgetDetailViewModel
                    )
                }
            }
        }
    }
}