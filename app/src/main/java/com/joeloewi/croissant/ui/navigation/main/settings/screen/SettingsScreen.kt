package com.joeloewi.croissant.ui.navigation.main.settings.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.joeloewi.croissant.R
import com.joeloewi.croissant.state.SettingsState
import com.joeloewi.croissant.state.rememberSettingsState
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.util.LocalActivity
import com.joeloewi.croissant.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsState = rememberSettingsState(
        navController = navController,
        settingsViewModel = settingsViewModel
    )

    SettingsContent(
        settingsState = settingsState
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsContent(
    settingsState: SettingsState,
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
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
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
                    headlineText = {
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
                        value = settingsState.settings.darkThemeEnabled,
                        role = Role.Switch,
                        onValueChange = settingsState::setDarkThemeEnabled
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = Icons.Default.DarkMode.name
                        )
                    },
                    headlineText = {
                        Text(text = stringResource(id = R.string.dark_theme))
                    },
                    supportingText = {
                        Text(
                            text = stringResource(id = R.string.always_use_dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = settingsState.settings.darkThemeEnabled,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item(
                key = "othersHeader"
            ) {
                ListItem(
                    headlineText = {
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
                        settingsState.onDeveloperInfoClick()
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = Icons.Default.Person.name
                        )
                    },
                    headlineText = {
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
                    headlineText = {
                        Text(text = stringResource(id = R.string.open_source_licenses))
                    }
                )
            }
        }
    }
}
