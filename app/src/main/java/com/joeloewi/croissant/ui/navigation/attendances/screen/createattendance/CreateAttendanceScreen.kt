package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.joeloewi.croissant.data.remote.model.common.GameRecord
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.GetSession
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SelectGames
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SetDetail
import com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable.SetTime
import com.joeloewi.croissant.viewmodel.CreateAttendanceViewModel
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

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
    val userInfo by createAttendanceViewModel.userInfo.collectAsState()
    val connectedGames by createAttendanceViewModel.connectedGames.collectAsState()

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.apply {
            get<String>("cookie")?.let {
                createAttendanceViewModel.setCookie(it)
            }
            remove<String>("cookie")
        }
    }

    CreateAttendanceContent(
        cookie = cookie,
        connectedGames = connectedGames,
        onLoginHoYoLAB = {
            navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
        },
    )
}

@ObsoleteCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceContent(
    cookie: String,
    connectedGames: Lce<List<GameRecord>>,
    onLoginHoYoLAB: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val onNextButtonClick: (() -> Unit) = {
        coroutineScope.launch {
            if (pagerState.currentPage < pagerState.pageCount) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    LaunchedEffect(cookie) {
        if (cookie.isNotEmpty() && pagerState.currentPage == 0) {
            pagerState.animateScrollToPage(1)
        }
    }

    Scaffold(
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val pages = remember {
                listOf(
                    CreateAttendancePage.GetSession,
                    CreateAttendancePage.SelectGames,
                    CreateAttendancePage.SetTime,
                    CreateAttendancePage.SetDetail
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
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(8.dp),
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
                                            checkedGames[gameRecord.hoYoLABGame] = true
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
                                SetTime(onNextButtonClick = onNextButtonClick)
                            }
                        }

                        CreateAttendancePage.SetDetail -> {
                            SetDetail()
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        bottom = 32.dp
                    )
                )
            }
        }
    }
}

sealed class CreateAttendancePage {
    object GetSession : CreateAttendancePage()
    object SelectGames : CreateAttendancePage()
    object SetTime : CreateAttendancePage()
    object SetDetail : CreateAttendancePage()
}