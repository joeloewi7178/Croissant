package com.joeloewi.croissant

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.material.color.DynamicColors
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.COOKIE
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.ResinStatusWidgetConfigurationNavigation
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.CreateResinStatusWidgetScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.LoadingScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.ResinStatusWidgetDetailScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.LoadingViewModel
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

        enableEdgeToEdge()
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
            CroissantTheme {
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
        Firebase.analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to currentDestination?.route,
                FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
            )
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            route = activity::class.java.simpleName,
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
                        navArgument(argument.first, argument.second)
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
                        navArgument(argument.first, argument.second)
                    },
                ) {
                    val newCookie by remember {
                        it.savedStateHandle.getStateFlow(COOKIE, "")
                    }.collectAsStateWithLifecycle()

                    CreateResinStatusWidgetScreen(
                        newCookie = { newCookie },
                        onClickAdd = {
                            navController.navigate(ResinStatusWidgetConfigurationDestination.LoginHoYoLABScreen.route)
                        }
                    )
                }

                composable(
                    route = ResinStatusWidgetConfigurationDestination.LoginHoYoLABScreen.route,
                ) {
                    LoginHoYoLABScreen(
                        onNavigateUp = {
                            navController.navigateUp()
                        },
                        onNavigateUpWithResult = {
                            navController.apply {
                                previousBackStackEntry?.savedStateHandle?.set(COOKIE, it)
                                navigateUp()
                            }
                        }
                    )
                }

                composable(
                    route = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen().route,
                    arguments = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen().arguments.map { argument ->
                        navArgument(argument.first, argument.second)
                    }
                ) {
                    ResinStatusWidgetDetailScreen()
                }
            }
        }
    }
}