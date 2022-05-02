package com.joeloewi.croissant

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.material.color.DynamicColors
import com.joeloewi.croissant.ui.navigation.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendanceDetailScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendanceLogsScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.AttendancesScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.LoginHoYoLABScreen
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.CreateAttendanceScreen
import com.joeloewi.croissant.ui.navigation.redemptioncodes.RedemptionCodesNavigation
import com.joeloewi.croissant.ui.navigation.redemptioncodes.screen.RedemptionCodesScreen
import com.joeloewi.croissant.ui.navigation.settings.SettingsDestination
import com.joeloewi.croissant.ui.navigation.settings.screen.SettingsScreen
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.ListItem
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.RequireAppUpdate
import com.joeloewi.croissant.util.RootChecker
import com.joeloewi.croissant.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

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
                    CompositionLocalProvider(LocalActivity provides this) {
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
        val (showRootedDeviceAlert, onShowRootedDeviceAlertChange) = remember { mutableStateOf(false) }

        LaunchedEffect(LocalLifecycleOwner.current.lifecycle.currentState) {
            if (RootChecker(context = context).isDeviceRooted()) {
                onShowRootedDeviceAlertChange(true)
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
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
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
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
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

                    NavigationBar(
                        modifier = Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .fillMaxWidth(),
                        containerColor = if (isFullScreenDestination) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ) {}
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
                            val redemptionCodesViewModel: RedemptionCodesViewModel = hiltViewModel()

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
                                Text(text = "확인")
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = Icons.Default.Warning.name
                            )
                        },
                        title = {
                            Text(text = "경고")
                        },
                        text = {
                            Text(text = "루팅이 감지되었습니다. 앱을 종료합니다.")
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
                    "알림 수신에 동의하였습니다. (일자 : ${readableTimestamp})"
                )
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                Column {
                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(DefaultDp),
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
                            Text(text = "권한 허가하고 시작하기")
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
                    text = "크루아상 시작하기",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "간편한 설정을 통해 HoYoLAB 자동 출석체크 및 원신 레진 알림 위젯 기능을 사용할 수 있습니다.",
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "시작하기 전에 다음 권한에 대한 허가가 필요합니다.",
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
}