package com.joeloewi.croissant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.joeloewi.croissant.ui.navigation.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.reminders.RemindersDestination
import com.joeloewi.croissant.ui.navigation.reminders.screen.RemindersScreen
import com.joeloewi.croissant.ui.navigation.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
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
                ProvideWindowInsets {
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
}

@ExperimentalMaterial3Api
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
                                        painter = rememberVectorPainter(
                                            image = croissantNavigation.filledIcon
                                        ),
                                        contentDescription = croissantNavigation.filledIcon.name
                                    )
                                } else {
                                    Icon(
                                        painter = rememberVectorPainter(
                                            image = croissantNavigation.outlinedIcon
                                        ),
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
                        .navigationBarsHeight()
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