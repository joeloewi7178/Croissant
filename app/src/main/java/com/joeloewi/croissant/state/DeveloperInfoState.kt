package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.joeloewi.croissant.viewmodel.DeveloperInfoViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class DeveloperInfoState(
    private val navController: NavHostController,
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

@Composable
fun rememberDeveloperInfoState(
    navController: NavHostController,
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