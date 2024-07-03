package com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.joeloewi.croissant.viewmodel.LoadingViewModel
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoadingScreen(
    loadingViewModel: LoadingViewModel = hiltViewModel(),
    onNavigateToCreateResinStatusWidget: (appWidgetId: Int) -> Unit,
    onNavigateToResinStatusWidgetDetail: (appWidgetId: Int) -> Unit,
) {
    loadingViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoadingViewModel.SideEffect.NavigateToCreateResinStatusWidget -> {
                onNavigateToCreateResinStatusWidget(sideEffect.appWidgetId)
            }

            is LoadingViewModel.SideEffect.NavigateToResinStatusWidgetDetail -> {
                onNavigateToResinStatusWidgetDetail(sideEffect.appWidgetId)
            }
        }
    }

    LoadingContent()
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .safeDrawingPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

        }
    }
}