package com.joeloewi.croissant.ui.navigation.attendances.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.VerticalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.joeloewi.croissant.ui.navigation.attendances.AttendancesDestination

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceScreen(
    navController: NavController
) {
    val (cookie, onCookieChange) = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        navController.currentBackStackEntry?.savedStateHandle?.apply {
            get<String>("cookie")?.let(onCookieChange)
            remove<String>("cookie")
        }
    }

    CreateAttendanceContent(
        cookie = cookie,
        onLoginHoYoLAB = {
            navController.navigate(AttendancesDestination.LoginHoYoLabScreen.route)
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalMaterial3Api
@Composable
fun CreateAttendanceContent(
    cookie: String,
    onLoginHoYoLAB: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                modifier = Modifier.padding(
                    rememberInsetsPaddingValues(
                        LocalWindowInsets.current.statusBars,
                        applyBottom = false,
                    )
                ),
                title = {
                    Text(text = "출석 작업 만들기")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val pagerState = rememberPagerState()

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
                modifier = Modifier.weight(1f),
            ) {
                VerticalPager(
                    state = pagerState,
                    count = pages.size,
                    contentPadding = PaddingValues(all = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    userScrollEnabled = false
                ) { page ->
                    when (pages[page]) {
                        CreateAttendancePage.GetSession -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                            ) {
                                Text(
                                    text = "접속 정보 가져오기",
                                    style = MaterialTheme.typography.headlineMedium
                                )

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "HoYoLAB 로그인",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "아래의 버튼을 눌러 표시되는 웹 화면의 HoYoLAB에 로그인 해주세요.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "로그인 중 접속정보가 확인되면 웹 화면이 자동으로 닫히고 다음 단계로 진행됩니다.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Text(
                                    text = "로그인 완료 후에도 진행되지 않는다면 웹 화면의 우측 상단 체크 버튼을 눌러주세요.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Card(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.error
                                ) {
                                    Row(
                                        modifier = Modifier.padding(all = 8.dp),
                                    ) {
                                        Icon(
                                            modifier = Modifier.padding(all = 8.dp),
                                            imageVector = Icons.Outlined.Warning,
                                            contentDescription = Icons.Outlined.Warning.name
                                        )
                                        Text(

                                            modifier = Modifier.padding(all = 8.dp),
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("경고: ")
                                                }
                                                append("SNS 계정을 통한 로그인은 지원되지 않으니 HoYoLAB 계정으로 로그인하시기 바랍니다.")
                                            },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(onClick = onLoginHoYoLAB) {
                                        Icon(
                                            imageVector = Icons.Outlined.Login,
                                            contentDescription = Icons.Outlined.Login.name
                                        )
                                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                        Text(
                                            text = "HoYoLAB 로그인하기",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }

                        CreateAttendancePage.SelectGames -> {
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