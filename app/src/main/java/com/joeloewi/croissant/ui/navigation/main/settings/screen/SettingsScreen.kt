package com.joeloewi.croissant.ui.navigation.main.settings.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.joeloewi.croissant.R
import com.joeloewi.croissant.ui.navigation.main.CroissantNavigation
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.ui.theme.ContentAlpha
import com.joeloewi.croissant.viewmodel.SettingsViewModel
import com.joeloewi.domain.entity.Settings

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()

    SettingsContent(
        settings = settings,
        onDarkThemeEnabledChange = settingsViewModel::setDarkThemeEnabled,
        onDeveloperInfoClick = {
            navController.navigate(SettingsDestination.DeveloperInfoScreen.route)
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun SettingsContent(
    settings: Settings,
    onDarkThemeEnabledChange: (Boolean) -> Unit,
    onDeveloperInfoClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = CroissantNavigation.Settings.resourceId))
                }
            )
        }
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
                        value = settings.darkThemeEnabled,
                        role = Role.Switch,
                        onValueChange = onDarkThemeEnabledChange
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
                            modifier = Modifier.alpha(ContentAlpha.medium),
                            text = stringResource(id = R.string.always_use_dark_theme)
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = settings.darkThemeEnabled,
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
                        onDeveloperInfoClick()
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
        }
    }
}
