package com.joeloewi.croissant.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joeloewi.domain.entity.Settings
import com.joeloewi.domain.usecase.SettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getSettingsUseCase: SettingsUseCase.GetSettings,
    private val updateSettingsUseCase: SettingsUseCase.SetIsFirstLaunch,
) : ViewModel() {
    private val _settings = getSettingsUseCase()

    val isFirstLaunch = _settings.map { it.isFirstLaunch }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Settings().isFirstLaunch
    )

    init {
        _settings.map { it.darkThemeEnabled }.distinctUntilChanged()
            .onEach { darkThemeEnabled ->
                if (darkThemeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }.launchIn(viewModelScope)
    }

    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            updateSettingsUseCase.invoke(isFirstLaunch)
        }
    }
}