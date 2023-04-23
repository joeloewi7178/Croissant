package com.joeloewi.croissant

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.joeloewi.croissant.state.CroissantAppState
import com.joeloewi.croissant.state.rememberCroissantAppState
import com.joeloewi.croissant.state.rememberMainState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.*
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.main.firstlaunch.FirstLaunchDestination
import com.joeloewi.croissant.ui.navigation.main.firstlaunch.screen.FirstLaunchScreen
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.RedemptionCodesDestination
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.main.settings.screen.DeveloperInfoScreen
import com.joeloewi.croissant.ui.navigation.main.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        DynamicColors.applyToActivityIfAvailable(this)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _mainActivityViewModel.darkThemeEnabled.onEach { darkThemeEnabled ->
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
                val mainState = rememberMainState(mainActivityViewModel = _mainActivityViewModel)

                CompositionLocalProvider(
                    LocalActivity provides this,
                    LocalHourFormat provides mainState.hourFormat
                ) {
                    RequireAppUpdate(
                        appUpdateResultState = mainState.appUpdateResultState
                    ) {
                        CroissantApp()
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialNavigationApi::class
)
@Composable
fun CroissantApp() {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val deepLinkUri = remember(context) {
        Uri.Builder()
            .scheme(context.getString(R.string.deep_link_scheme))
            .authority(context.packageName)
            .build()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycle by LocalLifecycleOwner.current.lifecycle.observeAsState()
    val croissantAppState = rememberCroissantAppState()
    val isFirstLaunch = croissantAppState.isFirstLaunch
    val isDeviceRooted = croissantAppState.isDeviceRooted
    val currentDestination = croissantAppState.currentDestination
    val isAllPermissionsGranted = croissantAppState.multiplePermissionsState.allPermissionsGranted

    LaunchedEffect(isFirstLaunch, isAllPermissionsGranted) {
        if (isFirstLaunch || !isAllPermissionsGranted) {
            croissantAppState.navController.navigate(FirstLaunchDestination.FirstLaunchScreen.route) {
                popUpTo(activity::class.java.simpleName) {
                    inclusive = true
                }
            }
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

    ModalBottomSheetLayout(
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        ),
        bottomSheetNavigator = croissantAppState.bottomSheetNavigator,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
        scrimColor = MaterialTheme.colorScheme.scrim
    ) {
        Scaffold(
            bottomBar = {
                if (croissantAppState.isBottomNavigationBarVisible) {
                    CroissantBottomNavigationBar(
                        croissantAppState = croissantAppState,
                    )
                }
            },
            contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row {
                    if (croissantAppState.isNavigationRailVisible) {
                        CroissantNavigationRail(
                            croissantAppState = croissantAppState
                        )
                    }
                    CroissantNavHost(
                        modifier = Modifier.animateContentSize(),
                        navController = croissantAppState.navController,
                        snackbarHostState = snackbarHostState,
                        deepLinkUri = { deepLinkUri }
                    )
                }
            }

            if (isDeviceRooted) {
                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(
                            onClick = {
                                activity.finish()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = Icons.Default.Warning.name
                        )
                    },
                    title = {
                        Text(text = stringResource(id = R.string.caution))
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.device_rooting_detected),
                            textAlign = TextAlign.Center
                        )
                    },
                    properties = DialogProperties(
                        dismissOnClickOutside = false,
                        dismissOnBackPress = false
                    )
                )
            }

            if (lifecycle == Lifecycle.Event.ON_RESUME) {
                if (!croissantAppState.canScheduleExactAlarms) {
                    AlertDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                                            context.startActivity(it)
                                        }
                                    }
                                }
                            ) {
                                Text(text = stringResource(id = R.string.confirm))
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = Icons.Default.Warning.name
                            )
                        },
                        title = {
                            Text(text = stringResource(id = R.string.caution))
                        },
                        text = {
                            Text(
                                textAlign = TextAlign.Center,
                                text = stringResource(id = R.string.schedule_exact_alarm_disabled)
                            )
                        },
                        properties = DialogProperties(
                            dismissOnClickOutside = false,
                            dismissOnBackPress = false
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun CroissantNavHost(
    modifier: Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    deepLinkUri: () -> Uri,
) {
    val activity = LocalActivity.current
    val currentDeepLinkUri by rememberUpdatedState(newValue = deepLinkUri())

    NavHost(
        modifier = modifier,
        navController = navController,
        route = activity::class.java.simpleName,
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
                    snackbarHostState = snackbarHostState,
                    attendancesViewModel = attendancesViewModel
                )
            }

            composable(route = AttendancesDestination.CreateAttendanceScreen.route) {
                val createAttendanceViewModel: CreateAttendanceViewModel =
                    hiltViewModel()

                CreateAttendanceScreen(
                    navController = navController,
                    createAttendanceViewModel = createAttendanceViewModel
                )
            }

            composable(
                route = AttendancesDestination.LoginHoYoLabScreen.route,
            ) {
                val loginHoYoLABViewModel: LoginHoYoLABViewModel =
                    hiltViewModel()

                LoginHoYoLABScreen(
                    navController = navController,
                    loginHoYoLABViewModel = loginHoYoLABViewModel
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
                            "$currentDeepLinkUri/${AttendancesDestination.AttendanceDetailScreen().route}"
                    }
                )
            ) {
                val attendanceDetailViewModel: AttendanceDetailViewModel =
                    hiltViewModel()

                AttendanceDetailScreen(
                    navController = navController,
                    attendanceDetailViewModel = attendanceDetailViewModel
                )
            }

            composable(
                route = AttendancesDestination.AttendanceLogsCalendarScreen().route,
                arguments = AttendancesDestination.AttendanceLogsCalendarScreen().arguments.map { argument ->
                    navArgument(argument.first) {
                        type = argument.second
                    }
                }
            ) {
                val attendanceLogsCalendarViewModel: AttendanceLogsCalendarViewModel =
                    hiltViewModel()

                AttendanceLogsCalendarScreen(
                    navController = navController,
                    attendanceLogsCalendarViewModel = attendanceLogsCalendarViewModel
                )
            }

            composable(
                route = AttendancesDestination.AttendanceLogsDayScreen().route,
                arguments = AttendancesDestination.AttendanceLogsDayScreen().arguments.map { argument ->
                    navArgument(argument.first) {
                        type = argument.second
                    }
                }
            ) {
                val attendanceLogsDayViewModel: AttendanceLogsDayViewModel =
                    hiltViewModel()

                AttendanceLogsDayScreen(
                    navController = navController,
                    attendanceLogsDayViewModel = attendanceLogsDayViewModel
                )
            }
        }

        navigation(
            startDestination = RedemptionCodesDestination.RedemptionCodesScreen.route,
            route = CroissantNavigation.RedemptionCodes.route
        ) {
            composable(route = RedemptionCodesDestination.RedemptionCodesScreen.route) {
                val redemptionCodesViewModel: RedemptionCodesViewModel =
                    hiltViewModel()

                RedemptionCodesScreen(
                    navController = navController,
                    redemptionCodesViewModel = redemptionCodesViewModel
                )
            }
        }

        navigation(
            startDestination = SettingsDestination.SettingsScreen.route,
            route = CroissantNavigation.Settings.route
        ) {
            composable(route = SettingsDestination.SettingsScreen.route) {
                SettingsScreen(navController = navController)
            }

            composable(route = SettingsDestination.DeveloperInfoScreen.route) {
                val developerInfoViewModel: DeveloperInfoViewModel = hiltViewModel()

                DeveloperInfoScreen(
                    navController = navController,
                    developerInfoViewModel = developerInfoViewModel
                )
            }
        }

        bottomSheet(route = FirstLaunchDestination.FirstLaunchScreen.route) {
            val firstLaunchViewModel: FirstLaunchViewModel = hiltViewModel()

            FirstLaunchScreen(
                navController = navController,
                firstLaunchViewModel = firstLaunchViewModel
            )
        }
    }
}

@Composable
private fun CroissantNavigationRail(
    croissantAppState: CroissantAppState,
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        header = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
    ) {
        croissantAppState.croissantNavigations.forEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected = croissantAppState.isSelected(route = croissantNavigation.route)

                NavigationRailItem(
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
                        croissantAppState.onClickNavigationButton(croissantNavigation.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun CroissantBottomNavigationBar(
    croissantAppState: CroissantAppState,
) {
    NavigationBar(
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
    ) {
        croissantAppState.croissantNavigations.forEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected = croissantAppState.isSelected(route = croissantNavigation.route)

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
                        croissantAppState.onClickNavigationButton(croissantNavigation.route)
                    }
                )
            }
        }
    }
}