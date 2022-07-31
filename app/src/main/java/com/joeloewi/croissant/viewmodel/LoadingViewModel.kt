package com.joeloewi.croissant.viewmodel

import android.appwidget.AppWidgetManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
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
    val appWidgetId =
        savedStateHandle.get<Int>("appWidgetId") ?: AppWidgetManager.INVALID_APPWIDGET_ID

    val isAppWidgetInitialized = flow {
        emit(Lce.Loading)
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
        started = SharingStarted.Lazily,
        initialValue = Lce.Loading
    )
}