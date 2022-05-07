package com.joeloewi.croissant

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.android.material.color.DynamicColors
import com.google.firebase.analytics.FirebaseAnalytics
import com.joeloewi.croissant.ui.theme.CroissantTheme
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.ProgressDialog
import com.joeloewi.croissant.util.isEmpty
import com.joeloewi.croissant.viewmodel.CreateResinStatusWidgetViewModel
import com.joeloewi.croissant.viewmodel.MainViewModel
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetConfigurationViewModel
import com.joeloewi.croissant.viewmodel.ResinStatusWidgetDetailViewModel
import com.joeloewi.domain.common.HoYoLABGame
import com.joeloewi.domain.entity.relational.AttendanceWithGames
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.AndroidEntryPoint

//app widget configuration intent does not provide app widget provider's name
@ExperimentalMaterial3Api
@AndroidEntryPoint
class ResinStatusWidgetConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
                        ResinStatusWidgetConfigurationApp()
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetConfigurationApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val resinStatusWidgetConfigurationViewModel: ResinStatusWidgetConfigurationViewModel =
        hiltViewModel()
    val isAppWidgetConfigured by resinStatusWidgetConfigurationViewModel.isAppWidgetInitialized.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current
    val appWidgetId = activity.intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

    LaunchedEffect(navController) {
        resinStatusWidgetConfigurationViewModel.findResinStatusWidgetByAppWidgetId(appWidgetId)
    }

    LaunchedEffect(navBackStackEntry?.destination) {
        FirebaseAnalytics.getInstance(context).logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            bundleOf(
                FirebaseAnalytics.Param.SCREEN_NAME to currentDestination?.route,
                FirebaseAnalytics.Param.SCREEN_CLASS to activity::class.java.simpleName
            )
        )
    }

    LaunchedEffect(isAppWidgetConfigured) {
        when (isAppWidgetConfigured) {
            is Lce.Content -> {
                if (isAppWidgetConfigured.content == true) {
                    navController.navigate("resinStatusWidgetDetail/${appWidgetId}")
                } else {
                    navController.navigate("createResinStatusWidget/${appWidgetId}")
                }
            }
            is Lce.Error -> {

            }
            Lce.Loading -> {
                navController.navigate("loading")
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
            Spacer(
                modifier = Modifier
                    .windowInsetsBottomHeight(WindowInsets.navigationBars)
                    .fillMaxWidth(),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "resinStatusWidgetConfiguration"
            ) {
                navigation(
                    startDestination = "loading",
                    route = "resinStatusWidgetConfiguration"
                ) {
                    composable(
                        route = "loading"
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    composable(
                        route = "createResinStatusWidget/{appWidgetId}",
                        arguments = listOf(
                            navArgument("appWidgetId") {
                                type = NavType.IntType
                            }
                        )
                    ) { navBackStackEntry ->
                        val createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel =
                            hiltViewModel(navBackStackEntry)

                        CreateResinStatusWidgetScreen(
                            navController = navController,
                            createResinStatusWidgetViewModel = createResinStatusWidgetViewModel
                        )
                    }

                    composable(
                        route = "resinStatusWidgetDetail/{appWidgetId}",
                        arguments = listOf(
                            navArgument("appWidgetId") {
                                type = NavType.IntType
                            }
                        )
                    ) { navBackStackEntry ->
                        val resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel =
                            hiltViewModel(navBackStackEntry)

                        ResinStatusWidgetDetailScreen(
                            navController = navController,
                            resinStatusWidgetDetailViewModel = resinStatusWidgetDetailViewModel
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetDetailScreen(
    navController: NavController,
    resinStatusWidgetDetailViewModel: ResinStatusWidgetDetailViewModel = hiltViewModel()
) {
    val selectableIntervals = resinStatusWidgetDetailViewModel.selectableIntervals
    val interval by resinStatusWidgetDetailViewModel.interval.collectAsState()
    val updateResinStatusWidgetState by resinStatusWidgetDetailViewModel.updateResinStatusWidgetState.collectAsState()

    ResinStatusWidgetDetailContent(
        selectableIntervals = selectableIntervals,
        interval = interval,
        updateResinStatusWidgetState = updateResinStatusWidgetState,
        onIntervalChange = resinStatusWidgetDetailViewModel::setInterval,
        onClickDone = resinStatusWidgetDetailViewModel::updateResinStatusWidget
    )
}

@ExperimentalMaterial3Api
@Composable
fun ResinStatusWidgetDetailContent(
    selectableIntervals: List<Long>,
    interval: Long,
    updateResinStatusWidgetState: Lce<Int>,
    onIntervalChange: (Long) -> Unit,
    onClickDone: () -> Unit
) {
    val activity = LocalActivity.current
    val (showProgressDialog, onShowProgressDialogChange) = mutableStateOf(false)

    BackHandler {
        activity.finish()
    }

    LaunchedEffect(updateResinStatusWidgetState) {
        when (updateResinStatusWidgetState) {
            is Lce.Content -> {
                if (updateResinStatusWidgetState.content != 0) {
                    onShowProgressDialogChange(false)
                    activity.finish()
                }
            }
            is Lce.Error -> {
                onShowProgressDialogChange(false)
            }
            Lce.Loading -> {
                onShowProgressDialogChange(true)
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "위젯 설정 상세")
                }
            )
        },
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultDp),
                onClick = onClickDone
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = Icons.Default.Done.name
                    )
                    Text(text = "수정 완료")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                text = "새로고침 간격",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = DefaultDp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                selectableIntervals.forEach {
                    Row(
                        modifier = Modifier.toggleable(
                            value = interval == it,
                            role = Role.RadioButton,
                            onValueChange = { checked ->
                                if (checked) {
                                    onIntervalChange(it)
                                }
                            }
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = DefaultDp,
                            alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        RadioButton(
                            selected = interval == it,
                            onClick = null
                        )

                        Text(text = "$it 분")
                    }
                }
            }
        }

        if (showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    onShowProgressDialogChange(false)
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetContent(
    appWidgetId: Int,
    selectableIntervals: List<Long>,
    createResinStatusWidgetState: Lce<List<Long>>,
    pagedAttendancesWithGames: LazyPagingItems<AttendanceWithGames>,
    checkedAttendanceIds: SnapshotStateList<Long>,
    interval: Long,
    onClickDone: () -> Unit,
    onIntervalChange: (Long) -> Unit
) {
    val activity = LocalActivity.current
    val (showProgressDialog, onShowProgressDialogChange) = mutableStateOf(false)

    LaunchedEffect(createResinStatusWidgetState) {
        when (createResinStatusWidgetState) {
            is Lce.Content -> {
                if (createResinStatusWidgetState.content.isNotEmpty()) {
                    onShowProgressDialogChange(false)

                    val resultValue = Intent().apply {
                        putExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            appWidgetId
                        )
                    }
                    with(activity) {
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
            is Lce.Error -> {
                onShowProgressDialogChange(false)
            }
            Lce.Loading -> {
                onShowProgressDialogChange(true)
            }
        }
    }

    BackHandler {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
            finish()
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "위젯 설정")
                }
            )
        },
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultDp),
                enabled = checkedAttendanceIds.isNotEmpty(),
                onClick = onClickDone
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = Icons.Default.Done.name
                    )
                    Text(text = "${checkedAttendanceIds.size}개 선택됨")
                }
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .then(Modifier.padding(DefaultDp)),
            verticalArrangement = Arrangement.spacedBy(
                space = DefaultDp,
            )
        ) {
            item(key = "intervalTitle") {
                Text(
                    text = "새로고침 간격",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item(key = "selectableIntervals") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {
                    selectableIntervals.forEach {
                        Row(
                            modifier = Modifier.toggleable(
                                value = interval == it,
                                role = Role.RadioButton,
                                onValueChange = { checked ->
                                    if (checked) {
                                        onIntervalChange(it)
                                    }
                                }
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                space = DefaultDp,
                                alignment = Alignment.CenterHorizontally
                            )
                        ) {
                            RadioButton(
                                selected = interval == it,
                                onClick = null
                            )

                            Text(text = "$it 분")
                        }
                    }
                }
            }

            item(key = "selectAccountTitle") {
                Text(
                    text = "레진을 확인할 계정",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (pagedAttendancesWithGames.isEmpty()) {
                item(key = "noAttendances") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DoubleDp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.fillMaxSize(0.3f),
                            imageVector = Icons.Default.Warning,
                            contentDescription = Icons.Default.Warning.name,
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                        Text(
                            text = "저장된 출석 작업이 없습니다.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "앱에서 먼저 출석 작업을 만들어주세요.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = pagedAttendancesWithGames,
                    key = { it.attendance.id }
                ) { item ->
                    if (item != null) {
                        AccountListItem(
                            item = item,
                            checkedAccounts = checkedAttendanceIds
                        )
                    } else {
                        AccountListItemPlaceholder()
                    }
                }
            }
        }

        if (showProgressDialog) {
            ProgressDialog(
                onDismissRequest = {
                    onShowProgressDialogChange(false)
                }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun CreateResinStatusWidgetScreen(
    navController: NavController,
    createResinStatusWidgetViewModel: CreateResinStatusWidgetViewModel
) {
    val selectableIntervals = createResinStatusWidgetViewModel.selectableIntervals
    val interval by createResinStatusWidgetViewModel.interval.collectAsState()
    val pagedAttendancesWithGames =
        createResinStatusWidgetViewModel.pagedAttendancesWithGames.collectAsLazyPagingItems()
    val checkedAttendanceIds = createResinStatusWidgetViewModel.checkedAttendanceIds
    val createResinStatusWidgetState by
    createResinStatusWidgetViewModel.createResinStatusWidgetState.collectAsState()
    val activity = LocalActivity.current

    LaunchedEffect(createResinStatusWidgetViewModel) {
        with(activity) {
            val resultValue = Intent().apply {
                putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    createResinStatusWidgetViewModel.appWidgetId
                )
            }
            setResult(Activity.RESULT_CANCELED, resultValue)
        }
    }

    CreateResinStatusWidgetContent(
        appWidgetId = createResinStatusWidgetViewModel.appWidgetId,
        selectableIntervals = selectableIntervals,
        createResinStatusWidgetState = createResinStatusWidgetState,
        pagedAttendancesWithGames = pagedAttendancesWithGames,
        checkedAttendanceIds = checkedAttendanceIds,
        interval = interval,
        onClickDone = createResinStatusWidgetViewModel::configureAppWidget,
        onIntervalChange = createResinStatusWidgetViewModel::setInterval
    )
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItem(
    item: AttendanceWithGames,
    checkedAccounts: SnapshotStateList<Long>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checkedAccounts.contains(item.attendance.id),
                enabled = item.games.any { it.type == HoYoLABGame.GenshinImpact },
                role = Role.Checkbox,
                onValueChange = { checked ->
                    if (checked) {
                        checkedAccounts.add(item.attendance.id)
                    } else {
                        checkedAccounts.remove(item.attendance.id)
                    }
                }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DoubleDp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.attendance.nickname)

            Checkbox(
                checked = checkedAccounts.contains(item.attendance.id),
                onCheckedChange = null
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun AccountListItemPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DoubleDp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                text = ""
            )

            Checkbox(
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.outline,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = MaterialTheme.colorScheme.background,
                        )
                    ),
                checked = false,
                onCheckedChange = null
            )
        }
    }
}