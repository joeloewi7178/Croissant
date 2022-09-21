package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.viewmodel.SettingsViewModel

@ExperimentalLifecycleComposeApi
@Stable
class SettingsState(
    val navController: NavController,
    val settingsViewModel: SettingsViewModel
) {
    val settings
        @Composable get() = settingsViewModel.settings.collectAsStateWithLifecycle().value

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean) {
        settingsViewModel.setDarkThemeEnabled(darkThemeEnabled = darkThemeEnabled)
    }

    fun onDeveloperInfoClick() {
        navController.navigate(SettingsDestination.DeveloperInfoScreen.route)
    }
}

@ExperimentalLifecycleComposeApi
@Composable
fun rememberSettingsState(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) = remember(
    navController,
    settingsViewModel
) {
    SettingsState(
        navController = navController,
        settingsViewModel = settingsViewModel
    )
}