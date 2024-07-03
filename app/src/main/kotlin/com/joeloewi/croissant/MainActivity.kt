package com.joeloewi.croissant

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.state.foldAsLce
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsCalendarScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsDayScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.main.global.GlobalDestination
import com.joeloewi.croissant.ui.navigation.main.global.screen.FirstLaunchScreen
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.RedemptionCodesDestination
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.main.settings.screen.DeveloperInfoScreen
import com.joeloewi.croissant.ui.navigation.main.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.RequireAppUpdate
import com.joeloewi.croissant.util.canScheduleExactAlarmsCompat
import com.joeloewi.croissant.util.useNavRail
import com.joeloewi.croissant.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectAsState

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val _mainActivityViewModel: MainActivityViewModel by viewModels()

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch(CoroutineExceptionHandler { _, _ -> }) {
            _mainActivityViewModel.container.stateFlow.mapLatest { it.darkThemeEnabled }
                .flowWithLifecycle(lifecycle)
                .flowOn(Dispatchers.IO).collect {
                    if (it) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                }
        }

        lifecycleScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
            Firebase.analytics.setUserId(
                Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
        }

        setContent {
            CroissantTheme {
                CompositionLocalProvider(
                    LocalActivity provides this
                ) {
                    val activityViewModel: MainActivityViewModel =
                        hiltViewModel(LocalActivity.current)
                    val state by activityViewModel.collectAsState()

                    CompositionLocalProvider(
                        LocalHourFormat provides state.hourFormat
                    ) {
                        RequireAppUpdate(
                            appUpdateResultState = state.appUpdateResult,
                        ) {
                            CroissantApp(state = state)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun CroissantApp(
    state: MainActivityViewModel.State
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
    val navController = rememberNavController()
    val windowSizeClass = calculateWindowSizeClass(activity = activity)
    val currentBackStackEntry by remember(navController) {
        navController.currentBackStackEntryFlow
    }.collectAsStateWithLifecycle(initialValue = null)
    val isNavigationRailVisible by remember {
        derivedStateOf {
            !state.fullScreenDestinations.any { route ->
                (currentBackStackEntry?.destination?.route?.contains(
                    route
                ) != false)
            } && windowSizeClass.useNavRail()
                    && currentBackStackEntry?.destination?.route == currentBackStackEntry?.destination?.parent?.startDestinationRoute
        }
    }
    val isBottomNavigationBarVisible by remember {
        derivedStateOf {
            !state.fullScreenDestinations.any { route ->
                (currentBackStackEntry?.destination?.route?.contains(
                    route
                ) != false)
            } && !windowSizeClass.useNavRail()
                    && currentBackStackEntry?.destination?.route == currentBackStackEntry?.destination?.parent?.startDestinationRoute
        }
    }

    LaunchedEffect(navController) {
        withContext(Dispatchers.IO + CoroutineExceptionHandler { _, _ -> }) {
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
                    croissantNavigations = state.croissantNavigations,
                    currentBackStackEntry = currentBackStackEntry,
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                if (isNavigationRailVisible) {
                    CroissantNavigationRail(
                        croissantNavigations = state.croissantNavigations,
                        currentBackStackEntry = currentBackStackEntry,
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
                Column(
                    modifier = remember(isNavigationRailVisible) {
                        Modifier
                            .fillMaxSize(1f)
                            .run {
                                if (isNavigationRailVisible) {
                                    navigationBarsPadding()
                                } else {
                                    this
                                }
                            }
                    }
                ) {
                    CroissantNavHost(
                        modifier = Modifier.fillMaxSize(1f),
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        deepLinkUri = deepLinkUri.toString(),
                        isFirstLaunch = state.isFirstLaunch
                    )
                }
            }
        }

        if (state.isDeviceRooted) {
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
    }
}

@Composable
fun CroissantNavHost(
    modifier: Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    deepLinkUri: String,
    isFirstLaunch: Boolean
) {
    val activity = LocalActivity.current
    val calculatedStartDestination by remember {
        snapshotFlow(isFirstLaunch).map { isFirstLaunch ->
            val anyOfPermissionsIsDenied = persistentListOf(
                CroissantPermission.AccessHoYoLABSession.permission,
                CroissantPermission.PostNotifications.permission
            ).any {
                ContextCompat.checkSelfPermission(
                    activity,
                    it
                ) == PackageManager.PERMISSION_DENIED
            } || activity.getSystemService<AlarmManager>()
                ?.canScheduleExactAlarmsCompat() == false

            isFirstLaunch || anyOfPermissionsIsDenied
        }.map {
            if (it) {
                GlobalDestination.FirstLaunchScreen.route
            } else {
                CroissantNavigation.Attendances.route
            }
        }.map {
            runCatching { it }.foldAsLce()
        }.catch {
            if (it !is CancellationException) {
                emit(runCatching { throw it }.foldAsLce<String>())
            }
        }
    }.collectAsStateWithLifecycle(initialValue = LCE.Loading)

    when (val cached = calculatedStartDestination) {
        is LCE.Content -> {
            NavHost(
                modifier = modifier,
                navController = navController,
                route = activity::class.java.simpleName,
                startDestination = cached.content
            ) {
                navigation(
                    startDestination = AttendancesDestination.AttendancesScreen.route,
                    route = CroissantNavigation.Attendances.route
                ) {
                    composable(route = AttendancesDestination.AttendancesScreen.route) {
                        AttendancesScreen(
                            snackbarHostState = snackbarHostState,
                            onClickCreateAttendance = {
                                navController.navigate(AttendancesDestination.CreateAttendanceScreen.route)
                            },
                            onClickAttendance = {
                                navController.navigate(
                                    AttendancesDestination.AttendanceDetailScreen.generateRoute(it)
                                )
                            }
                        )
                    }

                    composable(route = AttendancesDestination.CreateAttendanceScreen.route) {
                        val newCookie by remember {
                            it.savedStateHandle.getStateFlow(
                                AttendancesDestination.LoginHoYoLabScreen.COOKIE,
                                ""
                            )
                        }.collectAsStateWithLifecycle()

                        CreateAttendanceScreen(
                            newCookie = { newCookie },
                            onLoginHoYoLAB = {
                                navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
                            },
                            onNavigateToAttendanceDetailScreen = {
                                navController.navigate(
                                    AttendancesDestination.AttendanceDetailScreen.generateRoute(it)
                                ) {
                                    popUpTo(AttendancesDestination.CreateAttendanceScreen.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            onNavigateUp = {
                                navController.navigateUp()
                            }
                        )
                    }

                    composable(
                        route = AttendancesDestination.LoginHoYoLabScreen.route,
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
                        route = AttendancesDestination.AttendanceDetailScreen.route,
                        arguments = AttendancesDestination.AttendanceDetailScreen.arguments.map { argument ->
                            navArgument(argument.first, argument.second)
                        },
                        deepLinks = listOf(
                            navDeepLink {
                                uriPattern =
                                    "$deepLinkUri/${AttendancesDestination.AttendanceDetailScreen.route}"
                            }
                        )
                    ) { navBackStackEntry ->
                        val newCookie by remember {
                            navBackStackEntry.savedStateHandle.getStateFlow(
                                AttendancesDestination.LoginHoYoLabScreen.COOKIE,
                                ""
                            )
                        }.collectAsStateWithLifecycle()

                        AttendanceDetailScreen(
                            newCookie = { newCookie },
                            onNavigateUp = { navController.navigateUp() },
                            onClickRefreshSession = {
                                navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
                            },
                            onClickLogSummary = { attendanceId, loggableWorker ->
                                navController.navigate(
                                    AttendancesDestination.AttendanceLogsCalendarScreen.generateRoute(
                                        attendanceId,
                                        loggableWorker
                                    )
                                )
                            }
                        )
                    }

                    composable(
                        route = AttendancesDestination.AttendanceLogsCalendarScreen.route,
                        arguments = AttendancesDestination.AttendanceLogsCalendarScreen.arguments.map { argument ->
                            navArgument(argument.first, argument.second)
                        }
                    ) {
                        AttendanceLogsCalendarScreen(
                            onNavigateUp = { navController.navigateUp() },
                            onClickDay = { attendanceId, loggableWorker, localDate ->
                                navController.navigate(
                                    AttendancesDestination.AttendanceLogsDayScreen.generateRoute(
                                        attendanceId = attendanceId,
                                        loggableWorker = loggableWorker,
                                        localDate = localDate,
                                    )
                                )
                            }
                        )
                    }

                    composable(
                        route = AttendancesDestination.AttendanceLogsDayScreen.route,
                        arguments = AttendancesDestination.AttendanceLogsDayScreen.arguments.map { argument ->
                            navArgument(argument.first, argument.second)
                        }
                    ) {
                        AttendanceLogsDayScreen(
                            onNavigateUp = { navController.navigateUp() }
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
                        SettingsScreen(
                            onDeveloperInfoClick = {
                                navController.navigate(SettingsDestination.DeveloperInfoScreen.route)
                            }
                        )
                    }

                    composable(route = SettingsDestination.DeveloperInfoScreen.route) {
                        DeveloperInfoScreen(
                            onNavigateUp = { navController.navigateUp() }
                        )
                    }
                }

                composable(route = GlobalDestination.FirstLaunchScreen.route) {
                    FirstLaunchScreen(
                        onNavigateToAttendances = {
                            with(navController.graph) {
                                findNode(CroissantNavigation.Attendances.route)?.id?.let {
                                    setStartDestination(
                                        it
                                    )
                                }
                            }
                            navController.navigate(AttendancesDestination.AttendancesScreen.route) {
                                popUpTo(activity::class.java.simpleName) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }
            }
        }

        else -> {

        }
    }
}

@Composable
private fun CroissantNavigationRail(
    modifier: Modifier = Modifier,
    croissantNavigations: ImmutableList<CroissantNavigation>,
    currentBackStackEntry: NavBackStackEntry?,
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
        croissantNavigations.fastForEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected by remember(croissantNavigation.route, currentBackStackEntry) {
                    derivedStateOf { currentBackStackEntry?.destination?.hierarchy?.any { it.route == croissantNavigation.route } == true }
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
    currentBackStackEntry: NavBackStackEntry?,
    onClickNavigationButton: (String) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        croissantNavigations.fastForEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected by remember(croissantNavigation.route, currentBackStackEntry) {
                    derivedStateOf { currentBackStackEntry?.destination?.hierarchy?.any { it.route == croissantNavigation.route } == true }
                }

                NavigationBarItem(
                    modifier = Modifier,
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