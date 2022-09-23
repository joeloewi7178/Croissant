package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavController

@Stable
class DeveloperInfoState(
    private val navController: NavController
) {
    val previousBackStackEntry
        @Composable get() = navController.previousBackStackEntry

    fun onNavigateUp() {
        navController.navigateUp()
    }
}

@Composable
fun rememberDeveloperInfoState(
    navController: NavController
) = remember(navController) {
    DeveloperInfoState(
        navController = navController
    )
}