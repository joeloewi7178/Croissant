package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.joeloewi.croissant.data.local.model.Game
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.common.getResultFromPreviousComposable
import com.joeloewi.croissant.ui.common.navigationIconButton
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.COOKIE
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.GetSession
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SelectGames
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SetTime
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceScreen(
    navController: NavController,
    createAttendanceViewModel: CreateAttendanceViewModel
) {
    val cookie by createAttendanceViewModel.cookie.collectAsState()
    val connectedGames by createAttendanceViewModel.connectedGames.collectAsState()
    val createAttendanceState by createAttendanceViewModel.createAttendanceState.collectAsState()

    LaunchedEffect(createAttendanceViewModel) {
        getResultFromPreviousComposable<String>(
            navController = navController,
            key = COOKIE
        )?.let {
            createAttendanceViewModel.setCookie(cookie = it)
        }
    }

    CreateAttendanceContent(
        previousBackStackEntry = navController.previousBackStackEntry,
        cookie = cookie,
        connectedGames = connectedGames,
        createAttendanceState = createAttendanceState,
        onLoginHoYoLAB = {
            navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
        },
        onNavigateUp = navController::navigateUp,
        onCreateAttendance = {
            createAttendanceViewModel.createAttendance()
        }
    )
}

@ExperimentalFoundationApi
@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceContent(
    previousBackStackEntry: NavBackStackEntry?,
    cookie: String,
    connectedGames: Lce<List<GameRecord>>,
    createAttendanceState: Lce<List<Long>>,
    onLoginHoYoLAB: () -> Unit,
    onNavigateUp: () -> Unit,
    onCreateAttendance: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val onNextButtonClick: (() -> Unit) = {
        coroutineScope.launch {
            val nextPage = pagerState.currentPage + 1

            if (nextPage < pagerState.pageCount) {
                pagerState.scrollToPage(nextPage)
            } else if (nextPage == pagerState.pageCount) {
                onCreateAttendance()
            }
        }
    }
    val (showCancelConfirmationDialog, onShowCancelConfirmationDialogChange) = rememberSaveable {
        mutableStateOf(false)
    }
    val (showCreateAttendanceProgressDialog, onShowCreateAttendanceProgressDialogChange) = rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(cookie) {
        if (cookie.isNotEmpty() && pagerState.currentPage == 0) {
            pagerState.scrollToPage(1)
        }
    }

    LaunchedEffect(createAttendanceState) {
        when (createAttendanceState) {
            is Lce.Loading -> {
                onShowCreateAttendanceProgressDialogChange(true)
            }

            is Lce.Content -> {
                onShowCreateAttendanceProgressDialogChange(false)
                if (createAttendanceState.content.isNotEmpty()) {
                    onNavigateUp()
                }
            }

            is Lce.Error -> {
                onShowCreateAttendanceProgressDialogChange(false)
            }
        }
    }

    BackHandler(
        enabled = pagerState.currentPage <= 1
    ) {
        onShowCancelConfirmationDialogChange(true)
    }

    BackHandler(
        enabled = pagerState.currentPage > 1
    ) {
        coroutineScope.launch {
            pagerState.scrollToPage(pagerState.currentPage - 1)
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "출석 작업 만들기")
                },
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = previousBackStackEntry,
                    onClick = {
                        onShowCancelConfirmationDialogChange(true)
                    }
                )
            )
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(DefaultDp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val pages = remember {
                listOf(
                    CreateAttendancePage.GetSession,
                    CreateAttendancePage.SelectGames,
                    CreateAttendancePage.SetTime
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                HorizontalPager(
                    state = pagerState,
                    count = pages.size,
                    itemSpacing = DefaultDp,
                    contentPadding = PaddingValues(DefaultDp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    userScrollEnabled = false,
                    key = { it }
                ) { page ->
                    when (pages[page]) {
                        CreateAttendancePage.GetSession -> {
                            AnimatedVisibility(
                                visible = page == pagerState.currentPage,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                GetSession(
                                    onLoginHoYoLAB = onLoginHoYoLAB
                                )
                            }
                        }

                        CreateAttendancePage.SelectGames -> {
                            AnimatedVisibility(
                                visible = page == pagerState.currentPage,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                val createAttendanceViewModel: CreateAttendanceViewModel =
                                    hiltViewModel()
                                val checkedGames =
                                    remember { createAttendanceViewModel.checkedGames }

                                LaunchedEffect(connectedGames) {
                                    if (connectedGames is Lce.Content) {
                                        connectedGames.content.onEach { gameRecord ->
                                            checkedGames.add(
                                                Game(
                                                    name = gameRecord.hoYoLABGame,
                                                    region = gameRecord.region
                                                )
                                            )
                                        }
                                    }
                                }

                                SelectGames(
                                    checkedGames = checkedGames,
                                    connectedGames = connectedGames,
                                    onNextButtonClick = onNextButtonClick
                                )
                            }
                        }

                        CreateAttendancePage.SetTime -> {
                            AnimatedVisibility(
                                visible = page == pagerState.currentPage,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                val createAttendanceViewModel: CreateAttendanceViewModel =
                                    hiltViewModel()
                                val hourOfDay by createAttendanceViewModel.hourOfDay.collectAsState()
                                val minute by createAttendanceViewModel.minute.collectAsState()
                                val tickerCalendar by createAttendanceViewModel.tickerCalendar.collectAsState()

                                SetTime(
                                    hourOfDay = hourOfDay,
                                    minute = minute,
                                    tickerCalendar = tickerCalendar,
                                    onNextButtonClick = onNextButtonClick,
                                    onHourOfDayChange = createAttendanceViewModel::setHourOfDay,
                                    onMinuteChange = createAttendanceViewModel::setMinute
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showCancelConfirmationDialog) {
            AlertDialog(
                onDismissRequest = {
                    onShowCancelConfirmationDialogChange(false)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onShowCancelConfirmationDialogChange(false)
                            onNavigateUp()
                        }
                    ) {
                        Text(text = "확인")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onShowCancelConfirmationDialogChange(false)
                        }
                    ) {
                        Text(text = "취소")
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
                    Text(text = "이전 화면으로 돌아가게 되면 저장완료되지 않은 내용은 사라집니다. 계속하시겠습니까?")
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }

        if (showCreateAttendanceProgressDialog) {
            AlertDialog(
                onDismissRequest = {
                    onShowCreateAttendanceProgressDialogChange(false)
                },
                confirmButton = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Pending,
                        contentDescription = Icons.Default.Pending.name
                    )
                },
                title = {
                    Text(text = "저장 중")
                },
                text = {
                    Text(text = "잠시만 기다려주세요.")
                },
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            )
        }
    }
}

sealed class CreateAttendancePage {
    object GetSession : CreateAttendancePage()
    object SelectGames : CreateAttendancePage()
    object SetTime : CreateAttendancePage()
}