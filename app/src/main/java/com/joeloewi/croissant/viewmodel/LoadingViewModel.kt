package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.state.Lce
import com.joeloewi.domain.usecase.ResinStatusWidgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val getOneByAppWidgetIdResinStatusWidgetUseCase: ResinStatusWidgetUseCase.GetOneByAppWidgetId,
) : ViewModel() {
    private val _isAppWidgetInitialized = MutableStateFlow<Lce<Boolean>>(Lce.Loading)

    val isAppWidgetInitialized = _isAppWidgetInitialized.asStateFlow()

    fun findResinStatusWidgetByAppWidgetId(appWidgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAppWidgetInitialized.value = getOneByAppWidgetIdResinStatusWidgetUseCase
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
        }
    }
}