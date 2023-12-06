package com.joeloewi.croissant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.croissant.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val setDarkThemeEnabledSettingUseCase: SettingsUseCase.SetDarkThemeEnabled
) : ViewModel() {
    val darkThemeEnabled =
        getSettingsUseCase().map { it.darkThemeEnabled }.flowOn(Dispatchers.Default).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun setDarkThemeEnabled(darkThemeEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            setDarkThemeEnabledSettingUseCase(darkThemeEnabled)
        }
    }
}