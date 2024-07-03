package com.joeloewi.croissant

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.CreateResinStatusWidgetScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.LoadingScreen
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen.ResinStatusWidgetDetailScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.WidgetConfigurationActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

//app widget configuration intent does not provide app widget provider's name
@AndroidEntryPoint
class ResinStatusWidgetConfigurationActivity : AppCompatActivity() {
    private val _widgetConfigurationActivityViewModel: WidgetConfigurationActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            _widgetConfigurationActivityViewModel.container.stateFlow.flowWithLifecycle(lifecycle)
                .mapLatest { it.isDarkThemEnabled }.collect { isDarkThemEnabled ->
                    if (isDarkThemEnabled) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
        }

        setContent {
            CroissantTheme {
                CompositionLocalProvider(LocalActivity provides this) {
                    val activity = LocalActivity.current
                    val activityViewModel: WidgetConfigurationActivityViewModel =
                        hiltViewModel(activity)
                    val state by activityViewModel.collectAsState()
                    val navController = rememberNavController()

                    LaunchedEffect(navController, activity) {
                        navController.currentBackStackEntryFlow.flowOn(Dispatchers.IO).collect {
                            Firebase.analytics.logEvent(
                                FirebaseAnalytics.Event.SCREEN_VIEW,
                                bundleOf(
                                    FirebaseAnalytics.Param.SCREEN_NAME to it.destination.route,
                                    FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
                                )
                            )
                        }
                    }

                    ResinStatusWidgetConfigurationApp(
                        state = state,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun ResinStatusWidgetConfigurationApp(
    state: WidgetConfigurationActivityViewModel.State,
    navController: NavHostController,
) {
    Scaffold { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = ResinStatusWidgetConfigurationDestination.LoadingScreen.generateRoute(
                state.appWidgetId
            )
        ) {
            composable(
                route = ResinStatusWidgetConfigurationDestination.LoadingScreen.route,
                arguments = ResinStatusWidgetConfigurationDestination.LoadingScreen.arguments
            ) {
                LoadingScreen(
                    onNavigateToCreateResinStatusWidget = {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.generateRoute(
                                it
                            )
                        ) {
                            popUpTo(ResinStatusWidgetConfigurationDestination.LoadingScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToResinStatusWidgetDetail = {
                        navController.navigate(
                            ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen
                                .generateRoute(it)
                        ) {
                            popUpTo(ResinStatusWidgetConfigurationDestination.LoadingScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.route,
                arguments = ResinStatusWidgetConfigurationDestination.CreateResinStatusWidgetScreen.arguments,
            ) {
                val newCookie by remember {
                    it.savedStateHandle.getStateFlow(
                        AttendancesDestination.LoginHoYoLabScreen.COOKIE,
                        ""
                    )
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
                    onNavigateUp = { cookie ->
                        with(navController) {
                            if (cookie == null) {
                                navigateUp()
                            } else {
                                previousBackStackEntry?.savedStateHandle?.set(
                                    AttendancesDestination.LoginHoYoLabScreen.COOKIE,
                                    cookie
                                )
                                navigateUp()
                            }
                        }
                    }
                )
            }

            composable(
                route = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.route,
                arguments = ResinStatusWidgetConfigurationDestination.ResinStatusWidgetDetailScreen.arguments
            ) {
                ResinStatusWidgetDetailScreen()
            }
        }
    }
}