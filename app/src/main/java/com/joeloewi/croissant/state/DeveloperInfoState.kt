package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel

@ExperimentalLifecycleComposeApi
@Stable
class DeveloperInfoState(
    private val navController: NavController,
    private val developerInfoViewModel: DeveloperInfoViewModel
) {
    val previousBackStackEntry
        @Composable get() = navController.previousBackStackEntry

    val textToSpeech
        @Composable get() = developerInfoViewModel.textToSpeech.collectAsStateWithLifecycle().value

    fun onNavigateUp() {
        navController.navigateUp()
    }
}

@ExperimentalLifecycleComposeApi
@Composable
fun rememberDeveloperInfoState(
    navController: NavController,
    developerInfoViewModel: DeveloperInfoViewModel
) = remember(
    navController,
    developerInfoViewModel
) {
    DeveloperInfoState(
        navController = navController,
        developerInfoViewModel = developerInfoViewModel
    )
}