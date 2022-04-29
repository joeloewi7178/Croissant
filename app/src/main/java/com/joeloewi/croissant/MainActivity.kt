package com.joeloewi.croissant

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.DynamicColors
import com.joeloewi.croissant.ui.navigation.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendanceLogsScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.reminders.RemindersDestination
import com.joeloewi.croissant.ui.navigation.reminders.screen.RemindersScreen
import com.joeloewi.croissant.ui.navigation.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()

            CroissantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalActivity provides this) {
                        CroissantApp()
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
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
    val fullScreenDestinations = listOf(
        AttendancesDestination.CreateAttendanceScreen.route,
        AttendancesDestination.LoginHoYoLabScreen.route
    )
    val deepLinkUri = Uri.Builder()
        .scheme(stringResource(id = R.string.deep_link_scheme))
        .authority(LocalContext.current.packageName)
        .build()

    Scaffold(
        bottomBar = {
            Column {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val (isFullScreenDestination, onIsFullScreenDestinationChange) = rememberSaveable {
                    mutableStateOf(
                        false
                    )
                }

                LaunchedEffect(currentDestination) {
                    onIsFullScreenDestinationChange(
                        fullScreenDestinations.contains(
                            currentDestination?.route
                        )
                    )
                }
                if (!isFullScreenDestination) {
                    NavigationBar {
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
                }
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
                        val attendancesViewModel: AttendancesViewModel = hiltViewModel()

                        AttendancesScreen(
                            navController = navController,
                            attendancesViewModel = attendancesViewModel
                        )
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
                        LoginHoYoLABScreen(
                            navController = navController
                        )
                    }

                    composable(
                        route = AttendancesDestination.AttendanceDetailScreen().route,
                        arguments = AttendancesDestination.AttendanceDetailScreen().arguments.map { argument ->
                            navArgument(argument.first) {
                                type = argument.second
                            }
                        },
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern =
                                    "$deepLinkUri/${AttendancesDestination.AttendanceDetailScreen().route}"
                            }
                        )
                    ) { navBackStackEntry ->
                        val attendanceDetailViewModel: AttendanceDetailViewModel =
                            hiltViewModel(navBackStackEntry)

                        AttendanceDetailScreen(
                            navController = navController,
                            attendanceDetailViewModel = attendanceDetailViewModel
                        )
                    }

                    composable(
                        route = AttendancesDestination.AttendanceLogsScreen().route,
                        arguments = AttendancesDestination.AttendanceLogsScreen().arguments.map { argument ->
                            navArgument(argument.first) {
                                type = argument.second
                            }
                        }
                    ) { navBackStackEntry ->
                        val attendanceLogsViewModel: AttendanceLogsViewModel =
                            hiltViewModel(navBackStackEntry)

                        AttendanceLogsScreen(
                            navController = navController,
                            attendanceLogsViewModel = attendanceLogsViewModel
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