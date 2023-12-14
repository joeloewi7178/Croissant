package com.joeloewi.croissant.ui.navigation.main.global.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joeloewi.croissant.viewmodel.EmptyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

@Composable
fun EmptyScreen(
    emptyViewModel: EmptyViewModel = hiltViewModel(),
    onShowFirstLaunchScreen: () -> Unit,
    onShowDefaultScreen: () -> Unit,
) {
    val isFirstLaunch by emptyViewModel.isFirstLaunch.collectAsStateWithLifecycle()

    EmptyContent(
        isFirstLaunch = { isFirstLaunch },
        onShowFirstLaunchScreen = onShowFirstLaunchScreen,
        onShowDefaultScreen = onShowDefaultScreen
    )
}

@Composable
private fun EmptyContent(
    isFirstLaunch: () -> Boolean?,
    onShowFirstLaunchScreen: () -> Unit,
    onShowDefaultScreen: () -> Unit
) {

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            snapshotFlow { isFirstLaunch() }.catch { }.filterNotNull()
                .collect { showFirstLaunchScreen ->
                    withContext(Dispatchers.Main) {
                        if (showFirstLaunchScreen) {
                            onShowFirstLaunchScreen()
                        } else {
                            onShowDefaultScreen()
                        }
                    }
                }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

        }
    }
}