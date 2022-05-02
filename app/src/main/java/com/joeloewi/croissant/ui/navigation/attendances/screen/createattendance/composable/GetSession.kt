package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.joeloewi.croissant.ui.theme.DefaultDp

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
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = Icons.Default.Login.name
                    )
                    Text(
                        text = "HoYoLAB 로그인하기",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = DefaultDp)
        ) {
            Text(
                text = "접속 정보 가져오기",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "HoYoLAB 로그인",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "아래의 버튼을 눌러 표시되는 웹 화면의 HoYoLAB에 로그인 해주세요.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "로그인 중 접속정보가 확인되면 웹 화면이 자동으로 닫히고 다음 단계로 진행됩니다.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "로그인 완료 후에도 진행되지 않는다면 웹 화면의 우측 상단 체크 버튼을 눌러주세요.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ) {
                Row(
                    modifier = Modifier.padding(all = DefaultDp),
                ) {
                    Icon(
                        modifier = Modifier.padding(all = DefaultDp),
                        imageVector = Icons.Default.Warning,
                        contentDescription = Icons.Default.Warning.name
                    )
                    Text(
                        modifier = Modifier.padding(all = DefaultDp),
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