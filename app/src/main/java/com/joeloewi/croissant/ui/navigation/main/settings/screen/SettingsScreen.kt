package com.joeloewi.croissant.ui.navigation.main.settings.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.joeloewi.croissant.R
import com.joeloewi.croissant.util.ListItem
import com.joeloewi.croissant.util.Switch
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
        onDarkThemeEnabledChange = settingsViewModel::setDarkThemeEnabled
    )
}

@ExperimentalMaterialApi
@ExperimentalMaterial3Api
@Composable
fun SettingsContent(
    settings: Settings,
    onDarkThemeEnabledChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
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
                key = "themeEnabled"
            ) {
                ListItem(
                    text = {
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
                    icon = {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = Icons.Default.DarkMode.name
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.dark_theme))
                    },
                    secondaryText = {
                        Text(text = stringResource(id = R.string.always_use_dark_theme))
                    },
                    trailing = {
                        Switch(
                            checked = settings.darkThemeEnabled,
                            onCheckedChange = null
                        )
                    }
                )
            }
        }
    }
}
