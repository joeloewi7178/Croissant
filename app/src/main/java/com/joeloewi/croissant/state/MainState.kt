package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.viewmodel.MainViewModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Stable
class MainState(
    private val mainViewModel: MainViewModel
) {
    val hourFormat
        @Composable get() = mainViewModel.hourFormat.collectAsStateWithLifecycle().value

    val appUpdateResultState
        @Composable get() =
            mainViewModel.appUpdateResultState.collectAsStateWithLifecycle().value
}

@Composable
fun rememberMainState(
    mainViewModel: MainViewModel
) = remember(mainViewModel) {
    MainState(mainViewModel = mainViewModel)
}