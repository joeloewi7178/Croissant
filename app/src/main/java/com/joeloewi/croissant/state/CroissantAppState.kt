package com.joeloewi.croissant.state

import android.app.Activity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.isCompactWindowSize
import com.joeloewi.croissant.viewmodel.AppViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(
    ExperimentalLifecycleComposeApi::class,
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Stable
class CroissantAppState constructor(
    val multiplePermissionsState: MultiplePermissionsState,
    val modalBottomSheetState: ModalBottomSheetState,
    val bottomSheetNavigator: BottomSheetNavigator,
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

@OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class,
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun rememberCroissantAppState(
    multiplePermissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            CroissantPermission.AccessHoYoLABSession.permission,
            CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT
        )
    ),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    ),
    bottomSheetNavigator: BottomSheetNavigator = remember(modalBottomSheetState) {
        BottomSheetNavigator(modalBottomSheetState)
    },
    navController: NavHostController = rememberNavController(bottomSheetNavigator),
    croissantNavigations: ImmutableList<CroissantNavigation> = listOf(
        CroissantNavigation.Attendances,
        CroissantNavigation.RedemptionCodes,
        CroissantNavigation.Settings
    ).toImmutableList(),
    fullScreenDestinations: ImmutableList<String> = listOf(
        AttendancesDestination.CreateAttendanceScreen.route,
        AttendancesDestination.LoginHoYoLabScreen.route
    ).toImmutableList(),
    appViewModel: AppViewModel = hiltViewModel(),
    activity: Activity = LocalActivity.current,
    windowSizeClass: WindowSizeClass = calculateWindowSizeClass(activity = activity)
) = remember(
    multiplePermissionsState,
    modalBottomSheetState,
    bottomSheetNavigator,
    navController,
    croissantNavigations,
    fullScreenDestinations,
    appViewModel,
    activity,
    windowSizeClass
) {
    CroissantAppState(
        multiplePermissionsState = multiplePermissionsState,
        modalBottomSheetState = modalBottomSheetState,
        bottomSheetNavigator = bottomSheetNavigator,
        navController = navController,
        croissantNavigations = croissantNavigations,
        fullScreenDestinations = fullScreenDestinations,
        appViewModel = appViewModel,
        windowSizeClass = windowSizeClass
    )
}