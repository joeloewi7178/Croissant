package com.joeloewi.croissant

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.google.android.material.color.DynamicColors
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsCalendarScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsDayScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.COOKIE
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.main.global.GlobalDestination
import com.joeloewi.croissant.ui.navigation.main.global.screen.EmptyScreen
import com.joeloewi.croissant.ui.navigation.main.global.screen.FirstLaunchScreen
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.RedemptionCodesDestination
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.main.settings.screen.DeveloperInfoScreen
import com.joeloewi.croissant.ui.navigation.main.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.RequireAppUpdate
import com.joeloewi.croissant.util.isCompactWindowSize
import com.joeloewi.croissant.util.observeAsState
import com.joeloewi.croissant.viewmodel.AttendanceDetailViewModel
import com.joeloewi.croissant.viewmodel.AttendanceLogsCalendarViewModel
import com.joeloewi.croissant.viewmodel.AttendancesViewModel
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel
import com.joeloewi.croissant.viewmodel.FirstLaunchViewModel
import com.joeloewi.croissant.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _mainActivityViewModel: MainActivityViewModel by viewModels()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
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

        Firebase.analytics.setUserId(
            Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
        )

        setContent {
            CroissantTheme(
                window = { window }
            ) {
                val hourFormat by _mainActivityViewModel.hourFormat.collectAsStateWithLifecycle(
                    context = Dispatchers.Default
                )
                val appUpdateResultState by _mainActivityViewModel.appUpdateResultState.collectAsStateWithLifecycle(
                    context = Dispatchers.Default
                )
                val isDeviceRooted by _mainActivityViewModel.isDeviceRooted.collectAsStateWithLifecycle(
                    context = Dispatchers.Default
                )

                CompositionLocalProvider(
                    LocalActivity provides this,
                    LocalHourFormat provides hourFormat
                ) {
                    RequireAppUpdate(
                        appUpdateResultState = appUpdateResultState,
                    ) {
                        CroissantApp(
                            isDeviceRooted = isDeviceRooted
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CroissantApp(
    isDeviceRooted: Boolean
) {
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
    val navController = rememberNavController()
    val fullScreenDestinations = remember {
        listOf(
            AttendancesDestination.CreateAttendanceScreen.route,
            AttendancesDestination.LoginHoYoLabScreen.route
        ).toImmutableList()
    }
    val currentBackStackEntry by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
        initialValue = null,
        context = Dispatchers.Default
    )
    val windowSizeClass = calculateWindowSizeClass(activity = activity)
    val isBottomNavigationBarVisible by remember {
        derivedStateOf {
            fullScreenDestinations.contains(currentBackStackEntry?.destination?.route)
                    && windowSizeClass.isCompactWindowSize()
                    && currentBackStackEntry?.destination?.route == currentBackStackEntry?.destination?.parent?.startDestinationRoute
        }
    }
    val isNavigationRailVisible by remember {
        derivedStateOf {
            fullScreenDestinations.contains(currentBackStackEntry?.destination?.route) && windowSizeClass.isCompactWindowSize()
        }
    }
    val croissantNavigations = remember {
        listOf(
            CroissantNavigation.Attendances,
            CroissantNavigation.RedemptionCodes,
            CroissantNavigation.Settings
        ).toImmutableList()
    }

    LaunchedEffect(navController) {
        withContext(Dispatchers.Default + CoroutineExceptionHandler { _, _ -> }) {
            navController.currentBackStackEntryFlow.catch { }.collect {
                Firebase.analytics.logEvent(
                    FirebaseAnalytics.Event.SCREEN_VIEW,
                    bundleOf(
                        FirebaseAnalytics.Param.SCREEN_NAME to it.destination.route,
                        FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
                    )
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isBottomNavigationBarVisible) {
                CroissantBottomNavigationBar(
                    croissantNavigations = croissantNavigations,
                    currentBackStackEntry = { currentBackStackEntry },
                    onClickNavigationButton = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.statusBars)
            .exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                if (isNavigationRailVisible) {
                    CroissantNavigationRail(
                        croissantNavigations = croissantNavigations,
                        currentBackStackEntry = { currentBackStackEntry },
                        onClickNavigationButton = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                CroissantNavHost(
                    modifier = Modifier
                        .animateContentSize(),
                    navController = navController,
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

        /*if (lifecycle == Lifecycle.Event.ON_RESUME) {
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
        }*/
    }
}

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
        startDestination = CroissantNavigation.Global.route
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
                val newCookie by remember {
                    it.savedStateHandle.getStateFlow(COOKIE, "")
                }.collectAsStateWithLifecycle(context = Dispatchers.Default)

                AttendanceDetailScreen(
                    attendanceDetailViewModel = attendanceDetailViewModel,
                    newCookie = { newCookie },
                    onNavigateUp = { navController.navigateUp() },
                    onClickRefreshSession = {
                        navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
                    },
                    onClickLogSummary = { loggableWorker ->
                        navController.navigate(
                            AttendancesDestination.AttendanceLogsCalendarScreen().generateRoute(
                                attendanceDetailViewModel.attendanceId,
                                loggableWorker
                            )
                        )
                    }
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
                    attendanceLogsCalendarViewModel = attendanceLogsCalendarViewModel,
                    onNavigateUp = { navController.navigateUp() }
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
                AttendanceLogsDayScreen(
                    onNavigateUp = {
                        navController.navigateUp()
                    }
                )
            }
        }

        navigation(
            startDestination = RedemptionCodesDestination.RedemptionCodesScreen.route,
            route = CroissantNavigation.RedemptionCodes.route
        ) {
            composable(route = RedemptionCodesDestination.RedemptionCodesScreen.route) {
                RedemptionCodesScreen()
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

        navigation(
            startDestination = GlobalDestination.EmptyScreen.route,
            route = CroissantNavigation.Global.route
        ) {

            composable(route = GlobalDestination.EmptyScreen.route) {
                EmptyScreen(
                    onShowFirstLaunchScreen = {
                        navController.navigate(GlobalDestination.FirstLaunchScreen.route) {
                            popUpTo(activity::class.java.simpleName) {
                                inclusive = true
                            }
                        }
                    },
                    onShowDefaultScreen = {
                        navController.navigate(GlobalDestination.FirstLaunchScreen.route) {
                            popUpTo(activity::class.java.simpleName) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(route = GlobalDestination.FirstLaunchScreen.route) {
                val firstLaunchViewModel: FirstLaunchViewModel = hiltViewModel()

                FirstLaunchScreen(
                    navController = navController,
                    firstLaunchViewModel = firstLaunchViewModel
                )
            }
        }
    }
}

@Composable
private fun CroissantNavigationRail(
    modifier: Modifier = Modifier,
    croissantNavigations: ImmutableList<CroissantNavigation>,
    currentBackStackEntry: () -> NavBackStackEntry?,
    onClickNavigationButton: (String) -> Unit
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        header = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
    ) {
        croissantNavigations.forEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected by remember(croissantNavigation.route) {
                    derivedStateOf { currentBackStackEntry()?.destination?.hierarchy?.any { it.route == croissantNavigation.route } == true }
                }

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
                    onClick = { onClickNavigationButton(croissantNavigation.route) }
                )
            }
        }
    }
}

@Composable
private fun CroissantBottomNavigationBar(
    modifier: Modifier = Modifier,
    croissantNavigations: ImmutableList<CroissantNavigation>,
    currentBackStackEntry: () -> NavBackStackEntry?,
    onClickNavigationButton: (String) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        croissantNavigations.forEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected by remember(croissantNavigation.route) {
                    derivedStateOf { currentBackStackEntry()?.destination?.hierarchy?.any { it.route == croissantNavigation.route } == true }
                }

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
                        onClickNavigationButton(croissantNavigation.route)
                    }
                )
            }
        }
    }
}