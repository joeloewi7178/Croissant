package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    onDeveloperInfoClick: () -> Unit
) {
    val darkThemeEnabled by settingsViewModel.darkThemeEnabled.collectAsStateWithLifecycle(context = Dispatchers.Default)

    SettingsContent(
        darkThemeEnabled = { darkThemeEnabled },
        onDarkThemeEnabled = settingsViewModel::setDarkThemeEnabled,
        onDeveloperInfoClick = onDeveloperInfoClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    darkThemeEnabled: () -> Boolean,
    onDarkThemeEnabled: (Boolean) -> Unit,
    onDeveloperInfoClick: () -> Unit
) {
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.Settings.resourceId))
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item(
                key = "themeHeader"
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.theme),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "darkModeSwitch"
            ) {
                ListItem(
                    modifier = Modifier.toggleable(
                        value = darkThemeEnabled(),
                        role = Role.Switch,
                        onValueChange = onDarkThemeEnabled
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = Icons.Default.DarkMode.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.dark_theme))
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = R.string.always_use_dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = darkThemeEnabled(),
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "othersHeader"
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.others),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }

            item(
                key = "developerInfo"
            ) {
                ListItem(
                    modifier = Modifier.clickable {
                        onDeveloperInfoClick()
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = Icons.Default.Person.name
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.developer_info))
                    }
                )
            }

            item(
                key = "openSourceLicences"
            ) {
                ListItem(
                    modifier = Modifier.clickable {
                        with(activity) {
                            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                        }
                    },
                    leadingContent = {
                        Icon(
                            painter = rememberVectorPainter(
                                image = ImageVector.vectorResource(
                                    id = R.drawable.ic_baseline_open_source_24
                                )
                            ),
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(id = R.string.open_source_licenses))
                    }
                )
            }
        }
    }
}
