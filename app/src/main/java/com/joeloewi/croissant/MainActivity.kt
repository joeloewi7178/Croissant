package com.joeloewi.croissant

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.startup.AppInitializer
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.joeloewi.croissant.initializer.NotificationChannelInitializer
import com.joeloewi.croissant.state.CroissantAppState
import com.joeloewi.croissant.state.rememberCroissantAppState
import com.joeloewi.croissant.state.rememberMainState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendanceLogsScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.main.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.RedemptionCodesDestination
import com.joeloewi.croissant.ui.navigation.main.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.main.settings.screen.DeveloperInfoScreen
import com.joeloewi.croissant.ui.navigation.main.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.*
import com.joeloewi.croissant.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@ExperimentalLayoutApi
@FlowPreview
@ExperimentalLifecycleComposeApi
@ExperimentalMaterial3WindowSizeClassApi
@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        DynamicColors.applyToActivityIfAvailable(this)

        setContent {
            CroissantTheme(
                window = window
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = hiltViewModel()
                    val mainState = rememberMainState(mainViewModel = mainViewModel)

                    CompositionLocalProvider(
                        LocalActivity provides this,
                        LocalWindowSizeClass provides calculateWindowSizeClass(activity = this),
                        LocalHourFormat provides mainState.hourFormat
                    ) {
                        RequireAppUpdate(
                            appUpdateResultState = mainState.appUpdateResultState
                        ) {
                            CroissantApp(
                                mainViewModel = mainViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalLayoutApi
@FlowPreview
@ExperimentalLifecycleComposeApi
@ExperimentalPermissionsApi
@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@Composable
fun CroissantApp(
    mainViewModel: MainViewModel,
) {
    val croissantAppState = rememberCroissantAppState(
        navController = rememberNavController(),
        croissantNavigations = listOf(
            CroissantNavigation.Attendances,
            CroissantNavigation.RedemptionCodes,
            CroissantNavigation.Settings
        ).toImmutableList(),
        fullScreenDestinations = listOf(
            AttendancesDestination.CreateAttendanceScreen.route,
            AttendancesDestination.LoginHoYoLabScreen.route
        ).toImmutableList(),
        mainViewModel = mainViewModel
    )
    val isFirstLaunch = croissantAppState.isFirstLaunch
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            CroissantPermission.AccessHoYoLABSession.permission,
            CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT
        )
    )
    val isAllPermissionsGranted by remember(multiplePermissionsState) {
        derivedStateOf {
            multiplePermissionsState.allPermissionsGranted
        }
    }
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )
    val context = LocalContext.current
    val activity = LocalActivity.current
    val deepLinkUri = remember(context) {
        Uri.Builder()
            .scheme(context.getString(R.string.deep_link_scheme))
            .authority(context.packageName)
            .build()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val isDeviceRooted = croissantAppState.isDeviceRooted
    val currentDestination = croissantAppState.currentDestination
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(isFirstLaunch, isAllPermissionsGranted) {
        if (isFirstLaunch || !isAllPermissionsGranted) {
            modalBottomSheetState.show()
        } else {
            AppInitializer.getInstance(context)
                .initializeComponent(NotificationChannelInitializer::class.java)
            modalBottomSheetState.hide()
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
        sheetState = modalBottomSheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.surface),
        scrimColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.32f),
        sheetContent = {
            CroissantAppBottomSheetContent(
                multiplePermissionsState = multiplePermissionsState,
                modalBottomSheetState = modalBottomSheetState,
                snackbarHostState = snackbarHostState,
                onFirstLaunchChange = croissantAppState::setIsFirstLaunch
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (croissantAppState.isCompactWindowWidthSize) {
                    CroissantBottomNavigationBar(
                        croissantAppState = croissantAppState,
                    )
                }
            },
            contentWindowInsets = if (croissantAppState.isCompactWindowWidthSize) {
                WindowInsets(0, 0, 0, 0)
            } else {
                WindowInsets.navigationBars
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> {
                        CroissantNavHost(
                            modifier = Modifier
                                .fillMaxSize()
                                .animateContentSize(),
                            navController = croissantAppState.navController,
                            snackbarHostState = snackbarHostState,
                            deepLinkUri = { deepLinkUri }
                        )
                    }

                    else -> {
                        Row {
                            CroissantNavigationRail(
                                croissantAppState = croissantAppState
                            )
                            CroissantNavHost(
                                modifier = Modifier
                                    .weight(1f)
                                    .animateContentSize(),
                                navController = croissantAppState.navController,
                                snackbarHostState = snackbarHostState,
                                deepLinkUri = { deepLinkUri }
                            )
                        }
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

                if (!isFirstLaunch && !croissantAppState.isIgnoringBatteryOptimizations) {
                    AlertDialog(
                        onDismissRequest = {},
                        confirmButton = {
                            TextButton(
                                onClick = {
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

                if (!isFirstLaunch && !croissantAppState.canScheduleExactAlarms) {
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

@ExperimentalLayoutApi
@ExperimentalLifecycleComposeApi
@ExperimentalCoroutinesApi
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
    deepLinkUri: () -> Uri,
) {
    val currentDeepLinkUri by rememberUpdatedState(newValue = deepLinkUri())

    NavHost(
        modifier = modifier,
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
                            "$currentDeepLinkUri/${AttendancesDestination.AttendanceDetailScreen().route}"
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
                DeveloperInfoScreen(navController = navController)
            }
        }
    }
}

@ExperimentalLifecycleComposeApi
@Composable
private fun CroissantNavigationRail(
    croissantAppState: CroissantAppState,
) {
    NavigationRail(
        modifier = Modifier
            .run {
                if (!croissantAppState.isFullScreenDestination) {
                    this
                } else {
                    width(0.dp)
                }
            }
            .fillMaxHeight()
            .animateContentSize(),
        header = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(
                space = DoubleDp,
                alignment = Alignment.CenterVertically
            )
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
                            croissantAppState.navController.navigate(
                                croissantNavigation.route
                            ) {
                                popUpTo(croissantAppState.navController.graph.findStartDestination().id) {
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

@ExperimentalLifecycleComposeApi
@Composable
private fun CroissantBottomNavigationBar(
    croissantAppState: CroissantAppState,
) {
    val navigationBarHeightDp by rememberUpdatedState(
        newValue = if (croissantAppState.isBottomNavigationBarVisible) {
            Dp.Unspecified
        } else {
            0.dp
        }
    )

    NavigationBar(
        modifier = Modifier
            .height(navigationBarHeightDp)
            .animateContentSize()
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

@FlowPreview
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@ExperimentalPermissionsApi
@Composable
fun CroissantAppBottomSheetContent(
    multiplePermissionsState: MultiplePermissionsState,
    modalBottomSheetState: ModalBottomSheetState,
    snackbarHostState: SnackbarHostState,
    onFirstLaunchChange: (Boolean) -> Unit
) {
    val croissantPermissions = remember { CroissantPermission.values() }
    val context = LocalContext.current

    LaunchedEffect(multiplePermissionsState.allPermissionsGranted) {
        if (multiplePermissionsState.allPermissionsGranted) {
            val dateTimeFormatter =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            val localDateTime =
                Instant.now()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            val readableTimestamp = dateTimeFormatter.format(localDateTime)

            onFirstLaunchChange(false)

            if (modalBottomSheetState.isVisible) {
                snackbarHostState.showSnackbar(
                    context.getString(R.string.post_notification_granted, readableTimestamp)
                )
            }
        }
    }

    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
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
                        headlineText = {
                            Text(text = stringResource(id = item.label))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.icon.name
                            )
                        },
                        supportingText = {
                            Text(
                                text = stringResource(id = item.detailedDescription)
                            )
                        }
                    )
                }
            }
        }
    }
}