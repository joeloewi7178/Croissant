package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.LCE
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.util.navigationIconButton
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun DeveloperInfoScreen(
    developerInfoViewModel: DeveloperInfoViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val textToSpeech by developerInfoViewModel.textToSpeech.collectAsStateWithLifecycle(context = Dispatchers.IO)

    DeveloperInfoContent(
        textToSpeech = { textToSpeech },
        onNavigateUp = onNavigateUp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeveloperInfoContent(
    textToSpeech: () -> LCE<TextToSpeech>,
    onNavigateUp: () -> Unit
) {
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

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
                                enabled = textToSpeech() is LCE.Content
                            ) {
                                coroutineScope.launch(Dispatchers.IO) {
                                    textToSpeech().content?.runCatching {
                                        speak(
                                            "안아줘요",
                                            TextToSpeech.QUEUE_FLUSH,
                                            bundleOf(),
                                            "hug_me"
                                        )
                                    }
                                }
                            },
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
                            imageVector = Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = Icons.AutoMirrored.Default.OpenInNew.name
                        )
                    },
                    overlineContent = {
                        Text(text = "Github")
                    },
                    headlineContent = {
                        Text(text = developerGithub)
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
                            imageVector = Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = Icons.AutoMirrored.Default.OpenInNew.name
                        )
                    },
                    overlineContent = {
                        Text(text = stringResource(id = R.string.email))
                    },
                    headlineContent = {
                        Text(text = developerEmail)
                    }
                )
            }
        }
    }
}