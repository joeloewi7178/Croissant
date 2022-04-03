package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@ExperimentalMaterial3Api
@Composable
fun GetSession(
    onLoginHoYoLAB: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLoginHoYoLAB
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Login,
                        contentDescription = Icons.Outlined.Login.name
                    )
                    Text(
                        text = "HoYoLAB 로그인하기",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp)
        ) {
            item {
                Text(
                    text = "접속 정보 가져오기",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "HoYoLAB 로그인",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Text(
                    text = "아래의 버튼을 눌러 표시되는 웹 화면의 HoYoLAB에 로그인 해주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Text(
                    text = "로그인 중 접속정보가 확인되면 웹 화면이 자동으로 닫히고 다음 단계로 진행됩니다.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Text(
                    text = "로그인 완료 후에도 진행되지 않는다면 웹 화면의 우측 상단 체크 버튼을 눌러주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
            }
        }
    }
}