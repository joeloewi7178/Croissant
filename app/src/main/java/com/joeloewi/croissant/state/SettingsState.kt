package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.joeloewi.croissant.ui.navigation.main.settings.SettingsDestination
import com.joeloewi.croissant.viewmodel.SettingsViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class SettingsState(
    val navController: NavHostController,
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

@Composable
fun rememberSettingsState(
    navController: NavHostController,
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