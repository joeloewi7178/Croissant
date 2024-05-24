/*
 *    Copyright 2022 joeloewi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.joeloewi.croissant.ui.navigation.main.global.screen

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.SpecialPermission
import com.joeloewi.croissant.util.rememberSpecialPermissionState
import com.joeloewi.croissant.viewmodel.FirstLaunchViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

@Composable
fun FirstLaunchScreen(
    firstLaunchViewModel: FirstLaunchViewModel = hiltViewModel(),
    onNavigateToAttendances: () -> Unit
) {
    FirstLaunchContent(
        onFirstLaunchChange = firstLaunchViewModel::setIsFirstLaunch,
        onNavigateToAttendances = onNavigateToAttendances
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FirstLaunchContent(
    onFirstLaunchChange: (Boolean) -> Unit,
    onNavigateToAttendances: () -> Unit
) {
    val context = LocalContext.current
    val scheduleExactAlarmPermissionState =
        rememberSpecialPermissionState(specialPermission = SpecialPermission.ScheduleExactAlarms)
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            CroissantPermission.AccessHoYoLABSession.permission,
            CroissantPermission.PostNotifications.permission
        ),
        onPermissionsResult = {
            if (!scheduleExactAlarmPermissionState.status.isGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)

                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    }
                }
            }
        }
    )
    val croissantPermissions = remember { CroissantPermission.entries }

    LaunchedEffect(Unit) {
        combine(
            snapshotFlow { multiplePermissionsState.allPermissionsGranted },
            snapshotFlow { scheduleExactAlarmPermissionState.status == PermissionStatus.Granted }
        ) { allNormalPermissionsGranted, isScheduleExactAlarmPermitted ->
            allNormalPermissionsGranted && isScheduleExactAlarmPermitted
        }.catch { }.collect {
            if (it) {
                onFirstLaunchChange(false)
                onNavigateToAttendances()
            }
        }
    }

    Scaffold(
        bottomBar = {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = DefaultDp),
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
                    Text(text = stringResource(id = R.string.grant_permissions_and_start))
                }
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
            Spacer(modifier = Modifier.padding(DefaultDp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.extraSmall),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.mipmap.ic_launcher)
                        .build(),
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.start_croissant),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DefaultDp),
                text = stringResource(id = R.string.first_view_screen_description),
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DefaultDp),
                text = stringResource(id = R.string.before_start),
                textAlign = TextAlign.Center
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item(
                    key = "permissionsHeader"
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(id = R.string.permissions),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                items(
                    items = croissantPermissions,
                    key = { it.permission }
                ) { item ->

                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(id = item.label))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.icon.name
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(id = item.detailedDescription)
                            )
                        },
                        trailingContent = {
                            when (item) {
                                CroissantPermission.AccessHoYoLABSession, CroissantPermission.PostNotifications -> {
                                    if (multiplePermissionsState.permissions.find { it.permission == item.permission }?.status == PermissionStatus.Granted) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = Icons.Default.CheckCircle.name
                                        )
                                    }
                                }

                                CroissantPermission.ScheduleExactAlarms -> {
                                    if (scheduleExactAlarmPermissionState.status == PermissionStatus.Granted) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = Icons.Default.CheckCircle.name
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DefaultDp),
                colors = CardDefaults.cardColors(),
            ) {
                Row(
                    modifier = Modifier.padding(DefaultDp),
                ) {
                    Icon(
                        modifier = Modifier.padding(DefaultDp),
                        imageVector = Icons.Default.Star,
                        contentDescription = Icons.Default.Star.name
                    )
                    Text(
                        modifier = Modifier.padding(DefaultDp),
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(id = R.string.note))
                                append(": ")
                            }
                            append(stringResource(id = R.string.first_open_guidance_can_be_shown_again))
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}