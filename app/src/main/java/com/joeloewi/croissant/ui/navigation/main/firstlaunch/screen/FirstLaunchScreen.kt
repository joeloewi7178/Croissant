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

package com.joeloewi.croissant.ui.navigation.main.firstlaunch.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.FirstLaunchState
import com.joeloewi.croissant.state.rememberFirstLaunchState
import com.joeloewi.croissant.ui.theme.DefaultDp
import com.joeloewi.croissant.ui.theme.DoubleDp
import com.joeloewi.croissant.util.CroissantPermission
import com.joeloewi.croissant.util.createNotificationChannels
import com.joeloewi.croissant.viewmodel.FirstLaunchViewModel

@Composable
fun FirstLaunchScreen(
    navController: NavHostController,
    firstLaunchViewModel: FirstLaunchViewModel
) {
    val firstLaunchState = rememberFirstLaunchState(
        navController = navController,
        firstLaunchViewModel = firstLaunchViewModel
    )

    FirstLaunchContent(
        firstLaunchState = firstLaunchState
    )
}

@SuppressLint("BatteryLife")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun FirstLaunchContent(
    firstLaunchState: FirstLaunchState
) {
    val context = LocalContext.current
    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            CroissantPermission.AccessHoYoLABSession.permission,
            CroissantPermission.POST_NOTIFICATIONS_PERMISSION_COMPAT
        )
    )
    val isAllPermissionsGranted by remember(multiplePermissionsState) {
        derivedStateOf {
            multiplePermissionsState.allPermissionsGranted
        }
    }
    val croissantPermissions = remember { CroissantPermission.values() }

    LaunchedEffect(isAllPermissionsGranted) {
        if (isAllPermissionsGranted) {
            firstLaunchState.onFirstLaunchChange(false)
            context.createNotificationChannels()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    "package:${context.packageName}".toUri()
                ).also {
                    context.startActivity(it)
                }
            }
            firstLaunchState.navigateToAttendancesScreen()
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
        },
        contentWindowInsets = WindowInsets.safeDrawing
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
                        headlineText = {
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
                        headlineText = {
                            Text(text = stringResource(id = item.label))
                        },
                        leadingContent = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.icon.name
                            )
                        },
                        supportingText = {
                            Text(
                                text = stringResource(id = item.detailedDescription)
                            )
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