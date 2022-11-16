package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.DeveloperInfoState
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.state.rememberDeveloperInfoState
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalLifecycleComposeApi
@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@Composable
fun DeveloperInfoScreen(
    navController: NavController,
    developerInfoViewModel: DeveloperInfoViewModel
) {
    val developerInfoState = rememberDeveloperInfoState(
        navController = navController,
        developerInfoViewModel = developerInfoViewModel
    )

    DeveloperInfoContent(
        developerInfoState = developerInfoState
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@Composable
private fun DeveloperInfoContent(
    developerInfoState: DeveloperInfoState
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val textToSpeech = developerInfoState.textToSpeech

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = navigationIconButton(
                    previousBackStackEntry = developerInfoState.previousBackStackEntry,
                    onClick = developerInfoState::onNavigateUp
                ),
                title = {
                    Text(text = stringResource(id = R.string.developer_info))
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .consumedWindowInsets(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item(
                key = "baseInfo"
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(64.dp)
                            .clickable(
                                enabled = textToSpeech is Lce.Content
                            ) {
                                coroutineScope.launch(Dispatchers.Default) {
                                    textToSpeech.content?.runCatching {
                                        say("안아줘요", true)
                                    }
                                }
                            },
                        contentScale = ContentScale.Crop,
                        model = remember(context) {
                            ImageRequest.Builder(context)
                                .data(R.drawable.hug_me)
                                .build()
                        },
                        contentDescription = null
                    )
                    Text(text = "joeloewi", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = stringResource(id = R.string.android_app_developer)
                    )
                }
            }

            item(
                key = "websitesHeader"
            ) {
                ListItem(
                    headlineText = {
                        Text(
                            text = stringResource(id = R.string.websites),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "github"
            ) {
                val developerGithub = remember { "https://github.com/joeloewi7178" }
                val intent = remember {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(developerGithub)
                    )
                }

                ListItem(
                    modifier = Modifier.clickable(
                        enabled = remember {
                            intent.resolveActivity(activity.packageManager) != null
                        }
                    ) {
                        activity.startActivity(intent)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = Icons.Default.Public.name
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = Icons.Default.OpenInNew.name
                        )
                    },
                    overlineText = {
                        Text(text = "Github")
                    },
                    headlineText = {
                        Text(text = developerGithub)
                    }
                )
            }

            item(
                key = "contactsHeader"
            ) {
                ListItem(
                    headlineText = {
                        Text(
                            text = stringResource(id = R.string.contacts),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "email"
            ) {
                val developerEmail = remember { "joeloewi7178@gmail.com" }
                val intent = remember {
                    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
                        putExtra(Intent.EXTRA_EMAIL, developerEmail)
                    }
                }

                ListItem(
                    modifier = Modifier
                        .padding()
                        .clickable(
                            enabled = remember {
                                intent.resolveActivity(activity.packageManager) != null
                            }
                        ) {
                            activity.startActivity(intent)
                        },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = Icons.Default.Email.name
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = Icons.Default.OpenInNew.name
                        )
                    },
                    overlineText = {
                        Text(text = stringResource(id = R.string.email))
                    },
                    headlineText = {
                        Text(text = developerEmail)
                    }
                )
            }
        }
    }
}