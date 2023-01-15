package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import com.joeloewi.croissant.ui.navigation.widgetconfiguration.resinstatus.resinstatuswidgetconfiguration.ResinStatusWidgetConfigurationDestination
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _appWidgetIdKey =
        ResinStatusWidgetConfigurationDestination.LoadingScreen.APP_WIDGET_ID
    val appWidgetId =
        savedStateHandle.get<Int>(_appWidgetIdKey) ?: AppWidgetManager.INVALID_APPWIDGET_ID

    val isAppWidgetInitialized = flow<Lce<Boolean>> {
        emit(
            getOneByAppWidgetIdResinStatusWidgetUseCase
                .runCatching {
                    invoke(appWidgetId).resinStatusWidget.id
                }.fold(
                    onSuccess = {
                        Lce.Content(true)
                    },
                    onFailure = {
                        Lce.Content(false)
                    }
                )
        )
    }.flowOn(Dispatchers.IO).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Lce.Loading
    )
}