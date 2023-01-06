package com.joeloewi.croissant

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel
import com.joeloewi.croissant.viewmodel.WidgetConfigurationActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

//app widget configuration intent does not provide app widget provider's name
@AndroidEntryPoint
class ResinStatusWidgetConfigurationActivity : AppCompatActivity() {
    private val _widgetConfigurationActivityViewModel: WidgetConfigurationActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _widgetConfigurationActivityViewModel.darkThemeEnabled.onEach { darkThemeEnabled ->
                    if (darkThemeEnabled) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }.collect()
            }
        }

        setContent {
            CroissantTheme(
                window = window
            ) {
                CompositionLocalProvider(LocalActivity provides this) {
                    ResinStatusWidgetConfigurationApp()
                }
            }
        }
    }
}

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
        modifier = Modifier.safeDrawingPadding()
    ) {
        NavHost(
            navController = navController,
            route = "resinStatusWidgetConfiguration",
            startDestination = ResinStatusWidgetConfigurationNavigation.Configuration.route
        ) {
            navigation(
                startDestination = ResinStatusWidgetConfigurationDestination.EmptyScreen.route,
                route = ResinStatusWidgetConfigurationNavigation.Configuration.route
            ) {
                composable(
                    route = ResinStatusWidgetConfigurationDestination.EmptyScreen.route
                ) { navBackStackEntry ->
                    LaunchedEffect(navBackStackEntry) {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.LoadingScreen()
                                .generateRoute(appWidgetId)
                        ) {
                            popUpTo(navBackStackEntry.destination.id) {
                                inclusive = true
                            }
                        }
                    }
                }

                composable(
                    route = ResinStatusWidgetConfigurationDestination.LoadingScreen().route,
                    arguments = ResinStatusWidgetConfigurationDestination.LoadingScreen().arguments.map { argument ->
                        navArgument(argument.first) {
                            type = argument.second
                            defaultValue = AppWidgetManager.INVALID_APPWIDGET_ID
                        }
                    }
                ) {
                    val loadingViewModel: LoadingViewModel = hiltViewModel()

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
                ) {
                    val createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel =
                        hiltViewModel()

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
                ) {
                    val resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel =
                        hiltViewModel()

                    ResinStatusWidgetDetailScreen(
                        navController = navController,
                        resinStatusWidgetDetailViewModel = resinStatusWidgetDetailViewModel
                    )
                }
            }
        }
    }
}