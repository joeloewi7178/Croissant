package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.data.local.CroissantDatabase
import com.joeloewi.croissant.state.Lce
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResinStatusWidgetConfigurationViewModel @Inject constructor(
    private val croissantDatabase: CroissantDatabase
) : ViewModel() {
    private val _isAppWidgetInitialized = MutableStateFlow<Lce<Boolean>>(Lce.Loading)

    val isAppWidgetInitialized = _isAppWidgetInitialized.asStateFlow()

    fun findResinStatusWidgetByAppWidgetId(appWidgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isAppWidgetInitialized.value = croissantDatabase.resinStatusWidgetDao()
                .runCatching {
                    getOneByAppWidgetId(appWidgetId).resinStatusWidget.id
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