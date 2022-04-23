package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun SetDetail(
    onNextButtonClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNextButtonClick
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = DefaultDp,
                        alignment = Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = Icons.Default.ArrowForward.name
                    )
                    Text(text = "다음 단계로")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(space = DoubleDp)
        ) {
            item(
                key = "headline"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "세부 사항 설정하기",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item(
                key = "title"
            ) {
                Text(
                    modifier = Modifier.animateItemPlacement(),
                    text = "알림 및 추가 기능 설정",
                    style = MaterialTheme.typography.titleMedium
                )
            }

        }
    }
}