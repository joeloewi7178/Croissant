package com.joeloewi.croissant.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.viewmodel.MainActivityViewModel

@Stable
class MainState(
    private val mainActivityViewModel: MainActivityViewModel
) {
    val hourFormat
        @Composable get() = mainActivityViewModel.hourFormat.collectAsStateWithLifecycle().value

    val appUpdateResultState
        @Composable get() =
            mainActivityViewModel.appUpdateResultState.collectAsStateWithLifecycle().value
}

@Composable
fun rememberMainState(
    mainActivityViewModel: MainActivityViewModel
) = remember(mainActivityViewModel) {
    MainState(mainActivityViewModel = mainActivityViewModel)
}