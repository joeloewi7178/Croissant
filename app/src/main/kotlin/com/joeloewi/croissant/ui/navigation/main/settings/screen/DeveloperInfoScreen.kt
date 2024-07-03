package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun DeveloperInfoScreen(
    developerInfoViewModel: DeveloperInfoViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val activity = LocalActivity.current
    val state by developerInfoViewModel.collectAsState()

    developerInfoViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is DeveloperInfoViewModel.SideEffect.LaunchIntent -> {
                activity.startActivity(sideEffect.intent)
            }

            is DeveloperInfoViewModel.SideEffect.SpeakText -> {
                sideEffect.textToSpeech?.speak(
                    sideEffect.text,
                    TextToSpeech.QUEUE_FLUSH,
                    bundleOf(),
                    sideEffect.utteranceId
                )
            }
        }
    }

    DeveloperInfoContent(
        state = state,
        onSpeakText = developerInfoViewModel::onSpeakText,
        onLaunchIntent = developerInfoViewModel::onLaunchIntent,
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeveloperInfoContent(
    state: DeveloperInfoViewModel.State,
    onSpeakText: (text: String, utteranceId: String) -> Unit,
    onLaunchIntent: (intent: Intent) -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = LocalViewModelStoreOwner.current.navigationIconButton(
                    onClick = onNavigateUp
                ),
                title = {
                    Text(text = stringResource(id = R.string.developer_info))
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
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
                                enabled = state.isTTSInitialized
                            ) { onSpeakText("안아줘요", "hug_me") },
                        contentScale = ContentScale.Crop,
                        model = R.drawable.hug_me,
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
                    headlineContent = {
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
                ListItem(
                    modifier = Modifier.clickable(
                        enabled = state.canLaunchGithubIntent
                    ) { onLaunchIntent(state.githubIntent) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = Icons.Default.Public.name
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = Icons.AutoMirrored.Default.OpenInNew.name
                        )
                    },
                    overlineContent = {
                        Text(text = "Github")
                    },
                    headlineContent = {
                        Text(text = state.developerGithub)
                    }
                )
            }

            item(
                key = "contactsHeader"
            ) {
                ListItem(
                    headlineContent = {
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
                ListItem(
                    modifier = Modifier
                        .padding()
                        .clickable(
                            enabled = state.canLaunchEmailIntent
                        ) { onLaunchIntent(state.emailIntent) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = Icons.Default.Email.name
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = Icons.AutoMirrored.Default.OpenInNew.name
                        )
                    },
                    overlineContent = {
                        Text(text = stringResource(id = R.string.email))
                    },
                    headlineContent = {
                        Text(text = state.developerEmail)
                    }
                )
            }
        }
    }
}