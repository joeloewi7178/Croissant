package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.joeloewi.croissant.domain.usecase.ResinStatusWidgetUseCase
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    savedStateHandle: SavedStateHandle
) : ViewModel(), ContainerHost<LoadingViewModel.State, LoadingViewModel.SideEffect> {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.LoadingScreen.APP_WIDGET_ID

    override val container: Container<State, SideEffect> = container(State()) {
        intent {
            reduce {
                state.copy(
                    appWidgetId = savedStateHandle[_appWidgetIdKey]
                        ?: AppWidgetManager.INVALID_APPWIDGET_ID
                )
            }

            val isExists =
                runCatching { getOneByAppWidgetIdResinStatusWidgetUseCase(state.appWidgetId) }.isSuccess

            if (isExists) {
                postSideEffect(SideEffect.NavigateToResinStatusWidgetDetail(state.appWidgetId))
            } else {
                postSideEffect(SideEffect.NavigateToCreateResinStatusWidget(state.appWidgetId))
            }
        }
    }

    data class State(
        val appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    )

    sealed class SideEffect {
        data class NavigateToResinStatusWidgetDetail(
            val appWidgetId: Int
        ) : SideEffect()

        data class NavigateToCreateResinStatusWidget(
            val appWidgetId: Int
        ) : SideEffect()
    }
}