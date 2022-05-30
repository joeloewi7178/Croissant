package com.joeloewi.croissant

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.RedemptionCodesNavigation
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.main.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.util.ListItem
import com.joeloewi.croissant.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        installSplashScreen()

        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            CroissantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalActivity provides this,
                        LocalWindowSizeClass provides calculateWindowSizeClass(activity = this)
                    ) {
                        RequireAppUpdate {
                            CroissantApp()
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun CroissantApp() {
    val navController = rememberNavController()

    //global view model
    val mainViewModel: MainViewModel = hiltViewModel()
    val isFirstLaunch by mainViewModel.isFirstLaunch.collectAsState()

    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )

    val croissantNavigations = listOf(
        CroissantNavigation.Attendances,
        CroissantNavigation.RedemptionCodes,
        CroissantNavigation.Settings
    )
    val fullScreenDestinations = listOf(
        AttendancesDestination.CreateAttendanceScreen.route,
        AttendancesDestination.LoginHoYoLabScreen.route
    )
    val context = LocalContext.current
    val activity = LocalActivity.current
    val deepLinkUri = Uri.Builder()
        .scheme(stringResource(id = R.string.deep_link_scheme))
        .authority(context.packageName)
        .build()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch) {
            modalBottomSheetState.show()
        } else {
            modalBottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.32f),
        sheetContent = {
            CroissantAppBottomSheetContent(
                mainViewModel = mainViewModel,
                modalBottomSheetState = modalBottomSheetState,
                snackbarHostState = snackbarHostState
            )
        }
    ) {
        val lifecycle by LocalLifecycleOwner.current.lifecycle.observeAsState()
        val (showRootedDeviceAlert, onShowRootedDeviceAlertChange) = remember { mutableStateOf(false) }
        val (showBatteryOptimizationAlert, onShowBatteryOptimizationAlertChange) = remember {
            mutableStateOf(
                false
            )
        }

        LaunchedEffect(lifecycle) {
            when (lifecycle) {
                Lifecycle.Event.ON_RESUME -> {
                    onShowRootedDeviceAlertChange(
                        RootChecker(
                            context = context
                        ).isDeviceRooted()
                    )

                    onShowBatteryOptimizationAlertChange(
                        !context.isIgnoringBatteryOptimizations()
                    )
                }
                else -> {

                }
            }
        }
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val windowSizeClass = LocalWindowSizeClass.current
        val (isFullScreenDestination, onIsFullScreenDestinationChange) = rememberSaveable {
            mutableStateOf(
                false
            )
        }

        Scaffold(
            topBar = {
                Spacer(
                    modifier = Modifier.padding(
                        WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                            .asPaddingValues()
                    )
                )
            },
            bottomBar = {
                Column {
                    LaunchedEffect(currentDestination) {
                        FirebaseAnalytics.getInstance(context).logEvent(
                            FirebaseAnalytics.Event.SCREEN_VIEW,
                            bundleOf(
                                FirebaseAnalytics.Param.SCREEN_NAME to currentDestination?.route,
                                FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
                            )
                        )

                        onIsFullScreenDestinationChange(
                            fullScreenDestinations.contains(
                                currentDestination?.route
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = !isFullScreenDestination && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact,
                        enter = slideInVertically(
                            initialOffsetY = { it }
                        ) + fadeIn(),
                        exit = slideOutVertically(
                            targetOffsetY = { it }
                        ) + fadeOut()
                    ) {
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

                    Surface(
                        modifier = Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .fillMaxWidth(),
                        color = if (isFullScreenDestination) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        tonalElevation = if (isFullScreenDestination) {
                            0.dp
                        } else {
                            3.dp
                        }
                    ) {

                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        CroissantNavHost(
                            modifier = Modifier.fillMaxSize(),
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            deepLinkUri = deepLinkUri
                        )
                    }

                    else -> {
                        Row {
                            AnimatedVisibility(
                                visible = !isFullScreenDestination,
                                enter = slideInHorizontally() + fadeIn(),
                                exit = slideOutHorizontally() + fadeOut()
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
                                    croissantNavigations.forEach { croissantNavigation ->
                                        val isSelected = currentDestination?.hierarchy?.any {
                                            it.route == croissantNavigation.route
                                        } == true

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
                            CroissantNavHost(
                                modifier = Modifier
                                    .weight(1f),
                                navController = navController,
                                snackbarHostState = snackbarHostState,
                                deepLinkUri = deepLinkUri
                            )
                        }
                    }
                }

                if (showRootedDeviceAlert) {
                    AlertDialog(
                        onDismissRequest = {
                            onShowRootedDeviceAlertChange(false)
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onShowRootedDeviceAlertChange(false)
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

                if (!isFirstLaunch && showBatteryOptimizationAlert) {
                    AlertDialog(
                        onDismissRequest = {
                            onShowBatteryOptimizationAlertChange(false)
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    onShowBatteryOptimizationAlertChange(false)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).also {
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
                                text = stringResource(id = R.string.battery_optimization_enabled)
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

@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun CroissantNavHost(
    modifier: Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    deepLinkUri: Uri,
) {
    NavHost(
        modifier = modifier.animateContentSize(),
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
            startDestination = RedemptionCodesNavigation.RedemptionCodesScreen.route,
            route = CroissantNavigation.RedemptionCodes.route
        ) {
            composable(route = RedemptionCodesNavigation.RedemptionCodesScreen.route) {
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
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@Composable
fun CroissantAppBottomSheetContent(
    mainViewModel: MainViewModel,
    modalBottomSheetState: ModalBottomSheetState,
    snackbarHostState: SnackbarHostState,
) {
    val croissantPermissions = CroissantPermission.values()

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = croissantPermissions.map { it.permission }
    )
    val context = LocalContext.current

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {
            val dateTimeFormatter =
                DateTimeFormatter.ofLocalizedDate(org.threeten.bp.format.FormatStyle.SHORT)
            val localDateTime =
                Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            val readableTimestamp = dateTimeFormatter.format(localDateTime)

            mainViewModel.setIsFirstLaunch(false)

            if (modalBottomSheetState.isVisible) {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.post_notification_granted, readableTimestamp)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            Spacer(
                modifier = Modifier.padding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .asPaddingValues()
                )
            )
        },
        bottomBar = {
            Column {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DefaultDp)
                        .background(MaterialTheme.colorScheme.surface),
                    onClick = {
                        multiplePermissionsState.launchMultiplePermissionRequest()
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = Icons.Default.Checklist.name
                        )
                        Text(text = stringResource(id = R.string.grant_permissions_and_start))
                    }
                }

                Spacer(
                    modifier = Modifier
                        .windowInsetsBottomHeight(WindowInsets.navigationBars)
                        .fillMaxWidth(),
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = DoubleDp,
            )
        ) {
            Spacer(modifier = Modifier.padding(DoubleDp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    modifier = Modifier.size(48.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.mipmap.ic_launcher)
                        .build(),
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.start_croissant),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.first_view_screen_description),
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.before_start),
                textAlign = TextAlign.Center
            )

            LazyColumn {
                items(
                    items = croissantPermissions,
                    key = { it.permission }
                ) { item ->
                    ListItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.icon.name
                            )
                        },
                        secondaryText = {
                            Text(text = stringResource(id = item.detailedDescription))
                        },
                        text = {
                            Text(text = stringResource(id = item.label))
                        }
                    )
                }
            }
        }
    }
}