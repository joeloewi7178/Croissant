package com.joeloewi.croissant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.joeloewi.croissant.ui.navigation.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.reminders.RemindersDestination
import com.joeloewi.croissant.ui.navigation.reminders.screen.RemindersScreen
import com.joeloewi.croissant.ui.navigation.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import com.joeloewi.croissant.viewmodel.LoginHoYoLABViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            SideEffect {
                systemUiController.apply {
                    setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
                    setStatusBarColor(Color.Transparent, darkIcons = useDarkIcons)
                    setNavigationBarColor(Color.Transparent, darkIcons = useDarkIcons)
                }
            }

            CroissantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CroissantApp()
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun CroissantApp() {
    val navController = rememberNavController()
    val croissantNavigations = listOf(
        CroissantNavigation.Attendances,
        CroissantNavigation.Reminders,
        CroissantNavigation.Settings
    )

    Scaffold(
        bottomBar = {
            Column {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    croissantNavigations.forEach { croissantNavigation ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == croissantNavigation.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                if (isSelected) {
                                    Icon(
                                        imageVector = croissantNavigation.filledIcon,
                                        contentDescription = croissantNavigation.filledIcon.name
                                    )
                                } else {
                                    Icon(
                                        imageVector = croissantNavigation.outlinedIcon,
                                        contentDescription = croissantNavigation.outlinedIcon.name
                                    )
                                }
                            },
                            selected = isSelected,
                            label = {
                                Text(text = stringResource(id = croissantNavigation.resourceId))
                            },
                            onClick = {
                                navController.navigate(croissantNavigation.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
                Spacer(
                    Modifier
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = CroissantNavigation.Attendances.route
            ) {
                navigation(
                    startDestination = AttendancesDestination.AttendancesScreen.route,
                    route = CroissantNavigation.Attendances.route
                ) {
                    composable(route = AttendancesDestination.AttendancesScreen.route) {
                        AttendancesScreen(navController = navController)
                    }

                    composable(route = AttendancesDestination.CreateAttendanceScreen.route) {
                        val createAttendanceViewModel: CreateAttendanceViewModel = hiltViewModel()

                        CreateAttendanceScreen(
                            navController = navController,
                            createAttendanceViewModel = createAttendanceViewModel
                        )
                    }

                    composable(
                        route = AttendancesDestination.LoginHoYoLabScreen.route,
                    ) {
                        val loginHoYoLABViewModel: LoginHoYoLABViewModel = hiltViewModel()

                        LoginHoYoLABScreen(
                            navController = navController,
                            loginHoYoLABViewModel = loginHoYoLABViewModel
                        )
                    }
                }

                navigation(
                    startDestination = RemindersDestination.RemindersScreen.route,
                    route = CroissantNavigation.Reminders.route
                ) {
                    composable(route = RemindersDestination.RemindersScreen.route) {
                        RemindersScreen(navController = navController)
                    }
                }

                navigation(
                    startDestination = SettingsDestination.SettingsScreen.route,
                    route = CroissantNavigation.Settings.route
                ) {
                    composable(route = SettingsDestination.SettingsScreen.route) {
                        SettingsScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> rememberFlow(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(
        key1 = flow,
        key2 = lifecycleOwner
    ) { flow.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED) }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CroissantTheme {
        Greeting("Android")
    }
}