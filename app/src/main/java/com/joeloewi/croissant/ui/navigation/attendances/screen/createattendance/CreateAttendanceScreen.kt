package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.joeloewi.croissant.data.common.HoYoLABGame
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.rememberFlow
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.GetSession
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SelectGames
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceScreen(
    navController: NavController,
    createAttendanceViewModel: CreateAttendanceViewModel
) {
    val cookie by rememberFlow(
        flow = createAttendanceViewModel.cookie
    ).collectAsState(initial = "")

    val connectedGames by rememberFlow(
        flow = createAttendanceViewModel.connectedGames
    ).collectAsState(initial = Lce.Loading)

    val checkedGames = remember { createAttendanceViewModel.checkedGames }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.apply {
            get<String>("cookie")?.let { createAttendanceViewModel.setCookie(it) }
            remove<String>("cookie")
        }
    }

    CreateAttendanceContent(
        cookie = cookie,
        connectedGames = connectedGames,
        onLoginHoYoLAB = {
            navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
        },
        checkedGames = checkedGames
    )
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceContent(
    cookie: String,
    connectedGames: Lce<List<GameRecord>>,
    onLoginHoYoLAB: () -> Unit,
    checkedGames: SnapshotStateMap<HoYoLABGame, Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.padding(
                    WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .asPaddingValues()
                ),
                title = {
                    Text(text = "출석 작업 만들기")
                }
            )
        },
        floatingActionButton = {
            when (pagerState.currentPage) {
                1 -> {
                    val noGamesSelected = checkedGames.values.all { !it }

                    AnimatedVisibility(
                        visible = !noGamesSelected,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        ExtendedFloatingActionButton(
                            text = {
                                Text(text = "${checkedGames.values.filter { it }.size} 개 선택됨")
                            },
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Done,
                                    contentDescription = Icons.Outlined.Done.name
                                )
                            }
                        )
                    }
                }

                2 -> {
                    FloatingActionButton(
                        onClick = {

                        }
                    ) {

                    }
                }

                else -> {

                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val pages = remember {
                listOf(
                    CreateAttendancePage.GetSession,
                    CreateAttendancePage.SelectGames,
                    CreateAttendancePage.SetDetail
                )
            }

            LaunchedEffect(cookie) {
                if (cookie.isNotEmpty() && pagerState.currentPage == 0) {
                    pagerState.animateScrollToPage(1)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize(),
            ) {
                VerticalPager(
                    state = pagerState,
                    count = pages.size,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    userScrollEnabled = false
                ) { page ->
                    when (pages[page]) {
                        CreateAttendancePage.GetSession -> {
                            GetSession(onLoginHoYoLAB = onLoginHoYoLAB)
                        }

                        CreateAttendancePage.SelectGames -> {
                            SelectGames(
                                connectedGames = connectedGames,
                                checkedGames = checkedGames,
                                snackbarHostState = snackbarHostState
                            )
                        }

                        CreateAttendancePage.SetDetail -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "Step ${page + 1}",
                                    style = MaterialTheme.typography.headlineMedium
                                )

                                Text(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    text = "어쩌구 저쩌구",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    text = "어떤 게임에 출석할까요",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(onClick = onLoginHoYoLAB) {
                                        Text(
                                            text = "HoYoLAB 접속하기",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                VerticalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

sealed class CreateAttendancePage {
    object GetSession : CreateAttendancePage()
    object SelectGames : CreateAttendancePage()
    object SetDetail : CreateAttendancePage()
}