package com.joeloewi.croissant

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.joeloewi.croissant.state.LCE
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
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.LocalHourFormat
import com.joeloewi.croissant.util.RequireAppUpdate
import com.joeloewi.croissant.util.useNavRail
import com.joeloewi.croissant.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
                    val activity = LocalActivity.current
                    val snackbarHostState = remember { SnackbarHostState() }
                    val navController = rememberNavController()

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

                    activityViewModel.collectSideEffect { sideEffect ->
                        when (sideEffect) {
                            MainActivityViewModel.SideEffect.FinishActivity -> activity.finish()
                            is MainActivityViewModel.SideEffect.OnClickNavigationButton -> {
                                navController.navigate(sideEffect.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }

                    CompositionLocalProvider(
                        LocalHourFormat provides state.hourFormat
                    ) {
                        RequireAppUpdate(
                            appUpdateResultState = state.appUpdateResult,
                        ) {
                            CroissantApp(
                                state = state,
                                navController = navController,
                                snackbarHostState = snackbarHostState,
                                onClickNavigationButton = activityViewModel::onClickNavigationButton,
                                onClickConfirmClose = activityViewModel::onClickConfirmClose
                            )
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
    state: MainActivityViewModel.State,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    onClickNavigationButton: (String) -> Unit,
    onClickConfirmClose: () -> Unit
) {
    val activity = LocalActivity.current
    var isTopLevelDestination by rememberSaveable { mutableStateOf(false) }
    var isFullScreenDestination by rememberSaveable { mutableStateOf(false) }
    val windowSizeClass = calculateWindowSizeClass(activity = activity)
    val useNavRail = windowSizeClass.useNavRail()
    val isNavigationRailVisible by remember(
        useNavRail,
        isTopLevelDestination,
        isFullScreenDestination
    ) {
        derivedStateOf { !isFullScreenDestination && useNavRail && isTopLevelDestination }
    }
    val isBottomNavigationBarVisible by remember(
        useNavRail,
        isTopLevelDestination,
        isFullScreenDestination
    ) {
        derivedStateOf { !isFullScreenDestination && !useNavRail && isTopLevelDestination }
    }

    LaunchedEffect(navController, state.fullScreenDestinations) {
        navController.currentBackStackEntryFlow.catch { }.collect {
            isTopLevelDestination =
                it.destination.route == it.destination.parent?.startDestinationRoute
            isFullScreenDestination = it.destination.route in state.fullScreenDestinations
        }
    }

    when (state.startDestination) {
        is LCE.Content -> {
            Crossfade(
                modifier = Modifier.fillMaxSize(),
                targetState = useNavRail,
                label = ""
            ) { targetState ->
                if (targetState) {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AnimatedVisibility(
                            modifier = Modifier.fillMaxHeight(),
                            visible = isNavigationRailVisible,
                            enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                            exit = shrinkOut(shrinkTowards = Alignment.CenterStart) + fadeOut(),
                        ) {
                            CroissantNavigationRail(
                                croissantNavigations = state.croissantNavigations,
                                navController = navController,
                                onClickNavigationButton = onClickNavigationButton
                            )
                        }
                        CroissantNavHost(
                            modifier = Modifier
                                .run {
                                    if (isNavigationRailVisible) {
                                        navigationBarsPadding()
                                    } else {
                                        this
                                    }
                                }
                                .weight(1f)
                                .animateContentSize(),
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            route = state.route,
                            startDestination = state.startDestination.content
                        )
                    }
                } else {
                    Scaffold(
                        bottomBar = {
                            AnimatedVisibility(
                                modifier = Modifier.fillMaxWidth(),
                                visible = isBottomNavigationBarVisible,
                                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                                exit = shrinkOut(shrinkTowards = Alignment.BottomCenter) + fadeOut(),
                            ) {
                                CroissantBottomNavigationBar(
                                    croissantNavigations = state.croissantNavigations,
                                    navController = navController,
                                    onClickNavigationButton = onClickNavigationButton
                                )
                            }
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { innerPadding ->
                        CroissantNavHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .animateContentSize(),
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            route = state.route,
                            startDestination = state.startDestination.content
                        )
                    }
                }
            }
        }

        else -> {

        }
    }

    if (state.isDeviceRooted) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = onClickConfirmClose
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

@Composable
fun CroissantNavHost(
    modifier: Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    route: String,
    startDestination: String
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        route = route,
        startDestination = startDestination
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
                arguments = AttendancesDestination.AttendanceDetailScreen.arguments,
                deepLinks = AttendancesDestination.AttendanceDetailScreen.deepLinks
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
                arguments = AttendancesDestination.AttendanceLogsCalendarScreen.arguments
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
                arguments = AttendancesDestination.AttendanceLogsDayScreen.arguments
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
                        popUpTo(route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CroissantNavigationRail(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    croissantNavigations: ImmutableList<CroissantNavigation>,
    onClickNavigationButton: (String) -> Unit
) {
    val currentHierarchy = remember { SnapshotStateList<String>() }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { navBackStackEntry ->
            currentHierarchy.clear()
            currentHierarchy.addAll(navBackStackEntry.destination.hierarchy.mapNotNull { it.route })
        }
    }

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
                val isSelected by remember(croissantNavigation.route, currentHierarchy) {
                    derivedStateOf { croissantNavigation.route in currentHierarchy }
                }

                NavigationRailItem(
                    icon = {
                        Crossfade(
                            targetState = isSelected,
                            label = ""
                        ) { targetState ->
                            if (targetState) {
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
    navController: NavHostController,
    onClickNavigationButton: (String) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        croissantNavigations.fastForEach { croissantNavigation ->
            key(croissantNavigation.route) {
                val isSelected =
                    currentDestination?.hierarchy?.any { it.route == croissantNavigation.route } == true

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