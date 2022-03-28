package com.joeloewi.croissant.ui.navigation.attendances.screen.createattendance.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joeloewi.croissant.R

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun SetDetail() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                ) {
                    AsyncImage(
                        modifier = Modifier.size(24.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(R.drawable.ic_launcher_foreground)
                            .build(),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))

                    Text(text = stringResource(id = R.string.app_name))
                }

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "아무개의 (게임이름) 출석 "
                )

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "메시지"
                )
            }
        }
    }
}