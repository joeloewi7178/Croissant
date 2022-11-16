package com.joeloewi.croissant.state

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.isCompactWindowSize
import com.joeloewi.croissant.viewmodel.AppViewModel
import kotlinx.collections.immutable.ImmutableList

@ExperimentalLifecycleComposeApi
@Stable
class CroissantAppState(
    val navController: NavHostController,
    val croissantNavigations: ImmutableList<CroissantNavigation>,
    val fullScreenDestinations: ImmutableList<String>,
    private val appViewModel: AppViewModel,
    private val windowSizeClass: WindowSizeClass
) {
    val isFirstLaunch
        @Composable get() = appViewModel.isFirstLaunch.collectAsStateWithLifecycle().value

    val isDeviceRooted
        @Composable get() = appViewModel.isDeviceRooted.collectAsStateWithLifecycle().value

    private val navBackStackEntry
        @Composable get() = navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
            initialValue = null
        ).value

    val currentDestination
        @Composable get() = navBackStackEntry?.destination

    val isIgnoringBatteryOptimizations
        get() = appViewModel.isIgnoringBatteryOptimizations

    val isFullScreenDestination
        @Composable get() = fullScreenDestinations.contains(currentDestination?.route)

    val isCompactWindowSize
        get() = windowSizeClass.isCompactWindowSize()

    val isBottomNavigationBarVisible
        @Composable
        get() = !isFullScreenDestination && isCompactWindowSize

    val isNavigationRailVisible
        @Composable get() = !isFullScreenDestination && !isCompactWindowSize

    val canScheduleExactAlarms
        get() = appViewModel.canScheduleExactAlarms

    @Composable
    fun isSelected(route: String?): Boolean = currentDestination?.hierarchy?.any {
        it.route == route
    } == true

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        appViewModel.setIsFirstLaunch(isFirstLaunch)
    }

    fun onClickNavigationButton(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalLifecycleComposeApi
@Composable
fun rememberCroissantAppState(
    navController: NavHostController,
    croissantNavigations: ImmutableList<CroissantNavigation>,
    fullScreenDestinations: ImmutableList<String>,
    appViewModel: AppViewModel,
    activity: Activity = LocalActivity.current,
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(activity = activity)
) = remember(
    navController,
    croissantNavigations,
    fullScreenDestinations,
    appViewModel,
    activity,
    windowSizeClass
) {
    CroissantAppState(
        navController = navController,
        croissantNavigations = croissantNavigations,
        fullScreenDestinations = fullScreenDestinations,
        appViewModel = appViewModel,
        windowSizeClass = windowSizeClass
    )
}